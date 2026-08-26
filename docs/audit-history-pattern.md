# Audit History Pattern

This document explains the append-only audit-history convention added to this application's reference
tables — `locales`, `image_hosting_providers`/`image_hosting_provider_config_fields`, `countries`/
`country_locales`, `cities`/`city_locales`, `currencies`/`currency_locales`, `unit_types`/`unit_type_locales`,
`units`/`unit_locales`, and `service_categories`. It's an internal engineering reference, not an API
contract — none of this is exposed over REST today; it exists purely at the database layer.

---

## 1. The problem this solves

Every entity in this application already tracks `created_by`/`created_at`, `updated_by`/`updated_at`, and
soft-delete (`is_active`/`is_deleted`/`deleted_by`/`deleted_at`). Those columns only ever hold the *current*
state — they answer "who touched this row last," never "what did it look like before, or who changed it three
edits ago." If a category gets renamed twice, or a locale's config is edited, the previous values are gone the
moment the `UPDATE` commits.

The audit-history pattern adds a parallel, append-only table per audited table that answers exactly that
question, without changing how the original table is queried or written to day-to-day.

---

## 2. The shape: one history table per audited table

For an audited table `foo`, there is a `foo_history` table:

```sql
create table if not exists foo_history
(
    id         bigserial primary key,

    -- The foo row this history entry is a snapshot of. Not a FK: a
    -- history row must survive its parent row being hard-deleted (see §4).
    foo_id     bigint                       not null,

    -- Which kind of change produced this snapshot.
    operation  varchar(10)                  not null check (operation in ('INSERT', 'UPDATE', 'DELETE')),

    -- One column per mutable business column on foo, same name/type,
    -- holding foo's value for that column at the time of the change.
    <...foo's own columns, minus audit columns...>,

    -- Standard audit shape, kept for naming consistency with every
    -- other table — see §5 for why most of these are always static.
    created_by bigint references users (id) not null,
    created_at timestamp with time zone     not null default current_timestamp,
    updated_by bigint references users (id) not null,
    updated_at timestamp with time zone     not null default current_timestamp,
    version    bigint                       not null default 0,
    deleted_by bigint references users (id),
    deleted_at timestamp with time zone
);

create index if not exists idx_foo_history_foo on foo_history (foo_id);
```

Each row is a **full snapshot** of `foo` at one point in time — not a column-level diff. Reconstructing "what
did this row look like on date X" is a single `WHERE foo_id = ? AND created_at <= ?` query; computing a
human-readable diff between two snapshots (e.g. "name changed from X to Y") is left to the query/application
layer, typically with `LAG()` to compare each row to the one before it — nothing here computes that upfront.

---

## 3. The trigger: how rows get into `foo_history`

One `AFTER INSERT OR UPDATE OR DELETE` trigger per audited table writes the snapshot:

```sql
create or replace function trg_foo_audit()
    returns trigger as
$$
begin
    if (tg_op = 'INSERT') then
        insert into foo_history (foo_id, operation, <...columns...>, created_by, updated_by)
        values (new.id, 'INSERT', <...new.columns...>, new.created_by, new.created_by);
        return new;
    elsif (tg_op = 'UPDATE') then
        insert into foo_history (foo_id, operation, <...columns...>, created_by, updated_by)
        values (new.id, 'UPDATE', <...new.columns...>, new.updated_by, new.updated_by);
        return new;
    elsif (tg_op = 'DELETE') then
        insert into foo_history (foo_id, operation, <...columns...>, created_by, updated_by)
        values (old.id, 'DELETE', <...old.columns...>, old.deleted_by, old.deleted_by);
        return old;
    end if;
    return null;
end;
$$ language plpgsql;

drop trigger if exists trg_foo_audit on foo;
create trigger trg_foo_audit
    after insert or update or delete
    on foo
    for each row
execute function trg_foo_audit();
```

Because this is a database trigger rather than application-level logging, it fires no matter what wrote to
`foo` — the Spring app, a console SQL statement, or a future script — so there's no code path that can
silently skip writing history.

A second trigger makes `foo_history` itself append-only:

```sql
create or replace function fn_foo_history_immutable()
    returns trigger as
$$
begin
    raise exception 'foo_history rows are append-only and cannot be updated or deleted';
end;
$$ language plpgsql;

drop trigger if exists trg_foo_history_immutable on foo_history;
create trigger trg_foo_history_immutable
    before update or delete
    on foo_history
    for each row
execute function fn_foo_history_immutable();
```

Without this, the audit trail itself could be edited or deleted, which would defeat the point of having one.

---

## 4. Why the parent-pointer column is *not* a foreign key

The first version of this pattern gave `foo_id` a real `references foo (id)` constraint. That's wrong, and
breaks a real scenario: `trg_foo_audit` is `AFTER DELETE`, so by the time it runs
`INSERT INTO foo_history (foo_id, ...) VALUES (old.id, ...)`, the row `old.id` has *already been removed* from
`foo` within the same transaction. Postgres's MVCC rules mean a transaction always sees its own earlier
deletes as gone, so the FK check on that insert fails with a foreign key violation — the `DELETE` statement
that was supposed to be logged instead fails outright, and nothing gets recorded.

This app's convention is soft-delete only (`UPDATE ... SET is_deleted = true`, never a real `DELETE` — see the
root `CLAUDE.md`), so the `UPDATE` branch — the one actually used day to day — was never affected. Only the
`DELETE` branch was broken, and it stayed dormant until exercised directly (see
`sql-scripts/trigger_scenario_tests.sql`, TEST 8).

**The fix, applied everywhere:** `foo_id` (and the `foo_locale_id` equivalents on child history tables) is a
plain indexed `bigint`, never a foreign key. A history log has to be able to outlive the row it describes.

---

## 5. Why most of the standard audit columns are static here

`foo_history` carries the same `created_by/at`, `updated_by/at`, `version`, `deleted_by/at` shape as every
other table for naming consistency, but on a history row:

- `created_by`/`created_at` are meaningful — they record who made the underlying change and when.
- `updated_by`/`updated_at` are always set equal to `created_by`/`created_at`, since a history row is never
  actually updated after being written.
- `version` never increments, and `deleted_by`/`deleted_at` are always `null` — both are made impossible by
  the immutability trigger (§3).

There is deliberately **no separate `is_active`/`is_deleted` pair for the history row's own lifecycle** — that
would always be `true`/`false` forever (nothing can deactivate or delete a history row), carrying zero
information beyond "this row exists," which the table already tells you. Where `foo` itself has
`is_active`/`is_deleted` columns, those are reused as-is in the snapshot section — they mean "what was `foo`'s
own soft-delete state at the time of this change," not anything about the history row.

---

## 6. Where this is applied today

| Migration | Audited table(s) | History table(s) |
|---|---|---|
| `V2__create_locales_table.sql` | `locales` | `locale_history` |
| `V3__create_image_hosting_providers_table.sql` | `image_hosting_providers`, `image_hosting_provider_config_fields` | `image_hosting_provider_history`, `image_hosting_provider_config_field_history` |
| `V5__create_countries_table.sql` | `countries`, `country_locales` | `country_history`, `country_locale_history` |
| `V7__create_cities_table.sql` | `cities`, `city_locales` | `city_history`, `city_locale_history` |
| `V8__create_currencies_table.sql` | `currencies`, `currency_locales` | `currency_history`, `currency_locale_history` |
| `V9__create_unit_types_table.sql` | `unit_types`, `unit_type_locales` | `unit_type_history`, `unit_type_locale_history` |
| `V10__create_units_table.sql` | `units`, `unit_locales` | `unit_history`, `unit_locale_history` |
| `V11__create_service_categories_table.sql` | `service_categories` | `service_category_history` |

**Deliberately skipped:** `V4__create_image_hosting_provider_configs_table.sql`
(`image_hosting_provider_configs`). Its `config` column is `jsonb` holding live credentials (per the
`PASSWORD`-typed fields defined in `V3`) — snapshotting it into a history table would create a permanent,
ever-growing log of plaintext secrets. See §8.

---

## 7. Related integrity guards built alongside history

A few tables got additional triggers beyond plain history, because auditing a change and preventing a bad
change are different problems:

- **`service_categories` — cycle prevention** (`fn_prevent_service_category_cycle`, `V11`): blocks a category
  from being set as its own parent, or from forming a longer cycle (A→B→A), by walking the ancestor chain
  before allowing an `INSERT`/`UPDATE OF parent_id`.
- **`service_categories` — inactive-parent guard** (`fn_prevent_service_category_inactive_parent`, `V11`):
  blocks (re-)parenting a category under one that is soft-deleted, inactive, or `status = 'ARCHIVED'`.
- **`locales` — default-locale protection** (`fn_protect_default_locale`, `V2`): blocks deactivating,
  soft-deleting, or hard-deleting the locale with `code = 'en'`, since every entity's locale-fallback logic
  (`matchLocale`, see the root `CLAUDE.md`) assumes it always exists and is usable.
- **`currencies` — single active default** (`uq_currencies_single_default`, `V8`): a partial unique index on
  `(is_default) where is_default = true and is_active = true and is_deleted = false`, so at most one active
  currency can ever claim to be "the platform default" at a time.

**Deliberately skipped:**

- An equivalent "can't deactivate a provider still relied on" guard on `image_hosting_providers` — unlike the
  `locales`/`'en'` case, there's no documented evidence this app enforces "exactly one active provider," so
  adding a speculative guard risked blocking a legitimate admin action for no real reason.
- A guard blocking deactivation of a `locales` row that's still referenced by existing
  `country_locales`/`city_locales`/`currency_locales`/`unit_type_locales`/`unit_locales` rows. Hard `DELETE` of
  a referenced locale is already safely blocked by ordinary foreign-key `NO ACTION` semantics on those five
  tables; the soft-delete/deactivation case was left unenforced because the check would have to hardcode and
  maintain that list of five (growing) table names, and it was judged a rare enough admin mistake not to be
  worth that maintenance cost.

---

## 8. Known gaps

- **`image_hosting_provider_configs` has no history at all.** Its `config` jsonb can legitimately change
  (rotated API keys, new bucket), and none of that is tracked. If this is ever needed, the safe options are:
  redact `PASSWORD`-typed keys (per `image_hosting_provider_config_fields.field_type`) before snapshotting,
  track only non-secret metadata (`name`, `is_active`, `is_deleted`) and a boolean "secrets changed" flag, or
  keep relying on infra/secrets-management tooling outside this database entirely. See §6.
- **No diff/changelog view exists yet.** `foo_history` stores full snapshots (§2); turning that into a
  human-readable "field X changed from A to B" changelog requires a `LAG()`-based query or view that hasn't
  been built.
- **This pattern is new to the codebase, not retrofitted everywhere.** It was added specifically to the
  tables above; other entities added later should adopt it deliberately per-entity, not assume it's already
  wired up project-wide.
- **`sql-scripts/trigger_scenario_tests.sql`** exercises `service_categories`, `locales`, and `currencies`
  against these triggers with real INSERT/UPDATE/DELETE scenarios, including the one that caught the FK bug
  in §4. It only runs against real Postgres — `./mvnw test` uses H2 with Flyway disabled (see
  `src/test/resources/application.yaml`) and cannot execute PL/pgSQL, so this script is never part of the
  Maven test suite.

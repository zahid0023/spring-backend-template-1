# Inserting a Flyway Migration Mid-Sequence

This project's Flyway migrations live flat under `src/main/resources/db/migration/` — no
subfolders, every file named `V{n}__description.sql` directly in that one directory. Versions
must be unique across the whole directory.

## Use decimal sub-versions to insert

Flyway accepts multi-part version numbers (`V3.1__`, `V3.2__`, ...), which sort between `V3__` and
`V4__` without touching any other file. This is the standard way to insert a migration here:

```
src/main/resources/db/migration/V4__create_countries_table.sql
src/main/resources/db/migration/V3.1__add_country_flag_column.sql   <- new, runs right after V3
src/main/resources/db/migration/V7__create_cities_table.sql
```

To pick a number: find the migration your new one should logically run **after**, and append
`.1` (or `.2`, `.3`, ... if inserting multiple between the same pair). You only need to know that
one neighboring version — not the global max, and not every file in the directory.

## Appending at the end

For a genuinely new migration (not inserted between two existing ones), just use the next whole
number higher than the current max version in the directory.

## Full renumbering (rare, avoid if possible)

An earlier version of this doc described scripts that shifted every subsequent migration's
version up by one to physically free a slot (e.g. turning `V3` into `V4`, `V4` into `V5`, ...).
Those scripts (`scripts/insert-migration.sh` / `.ps1`) have been removed — decimal sub-versions
solve the same problem with no file churn and no risk of collisions between concurrently-edited
migrations.

If a genuine full renumber is ever needed (e.g. cleaning up an accumulation of decimal versions
back into whole numbers), remember:

- **Only safe if none of the affected migrations have been applied to a database anyone relies
  on** — your own local dev Postgres included. Flyway tracks applied migrations by version +
  checksum in `flyway_schema_history`; renaming an already-applied migration's file makes Flyway
  treat it as new; the next run will error (checksum/version mismatch) or attempt to reapply it.
- Renumbering by hand is a plain `git mv` per file, working from the **highest** version down to
  the insertion point, so no rename ever collides with a file that hasn't moved yet.
- This project has `out-of-order: true` set in `application.yaml`, which also lets a new migration
  simply take the next unused version number even though its intent logically belongs earlier —
  Flyway applies it in version order relative to what's already recorded, not creation order.

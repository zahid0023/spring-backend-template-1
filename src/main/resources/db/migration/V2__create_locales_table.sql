create table if not exists locales
(
    id         bigserial primary key,

    code       varchar(50)                  not null unique,
    name       varchar(255)                 not null,
    sort_order integer                      not null default 0,

    created_by bigint references users (id) not null,
    created_at timestamp with time zone     not null default current_timestamp,
    updated_by bigint references users (id) not null,
    updated_at timestamp with time zone     not null default current_timestamp,
    version    bigint                       not null default 0,
    is_active  boolean                      not null default true,
    is_deleted boolean                      not null default false,
    deleted_by bigint references users (id),
    deleted_at timestamp with time zone
);

insert into locales (code,
                     name,
                     sort_order,
                     created_by,
                     updated_by)
values ('en', 'English', 1, (select id from users where username = 'system'),
        (select id from users where username = 'system')),
       ('bn', 'Bengali', 2, (select id from users where username = 'system'),
        (select id from users where username = 'system'));


-- ============================================================
-- Audit history: append-only snapshot of every insert/update/
-- delete on locales, written by a trigger so it can't be
-- bypassed by any caller.
-- ============================================================

create table if not exists locale_history
(
    id         bigserial primary key,

    -- The locales row this history entry is a snapshot of. Not a FK: a
    -- history row must survive its parent row being hard-deleted.
    locale_id  bigint                       not null,

    -- Which kind of change produced this snapshot.
    operation  varchar(10)                  not null check (operation in ('INSERT', 'UPDATE', 'DELETE')),

    -- Snapshot of locales.code at the time of the change.
    code       varchar(50),

    -- Snapshot of locales.name at the time of the change.
    name       varchar(255),

    -- Snapshot of locales.sort_order at the time of the change.
    sort_order integer,

    -- Snapshot of locales.is_active at the time of the change.
    is_active  boolean,
    -- Snapshot of locales.is_deleted at the time of the change.
    is_deleted boolean,

    -- Who/when this history row was written — same person/time as the
    -- created_by/updated_by/deleted_by that triggered it, depending on operation.
    created_by bigint references users (id) not null,
    created_at timestamp with time zone     not null default current_timestamp,
    -- Kept for column-naming consistency with every other table; always equal
    -- to created_by/created_at since a history row is never actually updated.
    updated_by bigint references users (id) not null,
    updated_at timestamp with time zone     not null default current_timestamp,
    -- Kept for column-naming consistency; never increments, since updates are
    -- blocked entirely (see trg_locale_history_immutable below).
    version    bigint                       not null default 0,
    -- Kept for column-naming consistency; always null, since deletes are
    -- blocked entirely (see trg_locale_history_immutable below).
    deleted_by bigint references users (id),
    deleted_at timestamp with time zone
);

create index if not exists idx_locale_history_locale
    on locale_history (locale_id);

create or replace function trg_locales_audit()
    returns trigger as
$$
begin
    if (tg_op = 'INSERT') then
        insert into locale_history (locale_id, operation, code, name, sort_order, is_active, is_deleted,
                                     created_by, updated_by)
        values (new.id, 'INSERT', new.code, new.name, new.sort_order, new.is_active, new.is_deleted,
                new.created_by, new.created_by);
        return new;
    elsif (tg_op = 'UPDATE') then
        insert into locale_history (locale_id, operation, code, name, sort_order, is_active, is_deleted,
                                     created_by, updated_by)
        values (new.id, 'UPDATE', new.code, new.name, new.sort_order, new.is_active, new.is_deleted,
                new.updated_by, new.updated_by);
        return new;
    elsif (tg_op = 'DELETE') then
        insert into locale_history (locale_id, operation, code, name, sort_order, is_active, is_deleted,
                                     created_by, updated_by)
        values (old.id, 'DELETE', old.code, old.name, old.sort_order, old.is_active, old.is_deleted,
                old.deleted_by, old.deleted_by);
        return old;
    end if;
    return null;
end;
$$ language plpgsql;

drop trigger if exists trg_locales_audit on locales;
create trigger trg_locales_audit
    after insert or update or delete
    on locales
    for each row
execute function trg_locales_audit();

create or replace function fn_locale_history_immutable()
    returns trigger as
$$
begin
    raise exception 'locale_history rows are append-only and cannot be updated or deleted';
end;
$$ language plpgsql;

drop trigger if exists trg_locale_history_immutable on locale_history;
create trigger trg_locale_history_immutable
    before update or delete
    on locale_history
    for each row
execute function fn_locale_history_immutable();


-- ============================================================
-- Protects the 'en' locale from deactivation/deletion, since
-- every entity's locale fallback logic (matchLocale) assumes
-- it always exists and is usable.
-- ============================================================

create or replace function fn_protect_default_locale()
    returns trigger as
$$
begin
    if (tg_op = 'DELETE') then
        if old.code = 'en' then
            raise exception 'locale with code=en cannot be deleted; it is the fallback locale';
        end if;
        return old;
    end if;

    if (tg_op = 'UPDATE') then
        if old.code = 'en' and (new.is_deleted = true or new.is_active = false) then
            raise exception 'locale with code=en cannot be deactivated or soft-deleted; it is the fallback locale';
        end if;
        return new;
    end if;

    return new;
end;
$$ language plpgsql;

drop trigger if exists trg_protect_default_locale on locales;
create trigger trg_protect_default_locale
    before update or delete
    on locales
    for each row
execute function fn_protect_default_locale();

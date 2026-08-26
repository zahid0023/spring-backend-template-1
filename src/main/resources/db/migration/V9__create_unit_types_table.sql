create table if not exists unit_types
(
    id         bigserial primary key,

    -- WEIGHT
    -- VOLUME
    -- LENGTH
    -- AREA
    -- COUNT
    -- TIME
    -- TEMPERATURE
    -- ENERGY
    -- PRESSURE
    -- OTHER
    code       varchar(50)                  not null unique,
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

create table if not exists unit_type_locales
(
    id           bigserial primary key,

    unit_type_id bigint references unit_types (id) on delete cascade not null,
    locale_id    bigint references locales (id)                      not null,

    -- Weight
    -- Volume
    -- Length
    name         varchar(100)                                        not null,
    description  text                                                not null default '',
    sort_order   integer                                             not null default 0,

    created_by   bigint references users (id)                        not null,
    created_at   timestamp with time zone                            not null default current_timestamp,
    updated_by   bigint references users (id)                        not null,
    updated_at   timestamp with time zone                            not null default current_timestamp,
    version      bigint                                              not null default 0,
    is_active    boolean                                             not null default true,
    is_deleted   boolean                                             not null default false,
    deleted_by   bigint references users (id),
    deleted_at   timestamp with time zone,

    constraint uq_unit_type_locale
        unique (unit_type_id, locale_id)
);

DO
$$
    DECLARE
        sys_id bigint;
    BEGIN
        SELECT id INTO sys_id FROM users WHERE username = 'system';

        -- =============================================
        -- 1. Unit Types
        -- =============================================
        INSERT INTO unit_types (code, sort_order, created_by, updated_by)
        VALUES ('WEIGHT', 1, sys_id, sys_id),
               ('VOLUME', 2, sys_id, sys_id),
               ('LENGTH', 3, sys_id, sys_id),
               ('AREA', 4, sys_id, sys_id),
               ('COUNT', 5, sys_id, sys_id),
               ('TIME', 6, sys_id, sys_id),
               ('TEMPERATURE', 7, sys_id, sys_id),
               ('ENERGY', 8, sys_id, sys_id),
               ('PRESSURE', 9, sys_id, sys_id),
               ('OTHER', 10, sys_id, sys_id)
        ON CONFLICT (code) DO NOTHING;

        -- =============================================
        -- 2. Unit Type Locales (English)
        -- =============================================
        INSERT INTO unit_type_locales (unit_type_id, locale_id, name, description, sort_order, created_by, updated_by)
        SELECT ut.id, l.id, v.name, v.description, v.sort_order, sys_id, sys_id
        FROM unit_types ut
                 JOIN (VALUES ('WEIGHT', 'en', 'Weight', 'Units for measuring mass or weight.', 1),
                              ('VOLUME', 'en', 'Volume', 'Units for measuring liquid or gas volume.', 2),
                              ('LENGTH', 'en', 'Length', 'Units for measuring distance or length.', 3),
                              ('AREA', 'en', 'Area', 'Units for measuring surface area.', 4),
                              ('COUNT', 'en', 'Count', 'Units for counting discrete items.', 5),
                              ('TIME', 'en', 'Time', 'Units for measuring duration.', 6),
                              ('TEMPERATURE', 'en', 'Temperature', 'Units for measuring temperature.', 7),
                              ('ENERGY', 'en', 'Energy', 'Units for measuring energy or power.', 8),
                              ('PRESSURE', 'en', 'Pressure', 'Units for measuring pressure.', 9),
                              ('OTHER', 'en', 'Other', 'Other miscellaneous unit types.',
                               10)) v(code, locale_code, name, description, sort_order)
                      ON ut.code = v.code
                 JOIN locales l ON l.code = v.locale_code
        ON CONFLICT (unit_type_id, locale_id) DO NOTHING;

        -- =============================================
        -- 3. Unit Type Locales (Bengali)
        -- =============================================
        INSERT INTO unit_type_locales (unit_type_id, locale_id, name, description, sort_order, created_by, updated_by)
        SELECT ut.id, l.id, v.name, v.description, v.sort_order, sys_id, sys_id
        FROM unit_types ut
                 JOIN (VALUES ('WEIGHT', 'bn', 'ওজন', 'ভর বা ওজন পরিমাপের একক।', 1),
                              ('VOLUME', 'bn', 'আয়তন', 'তরল বা গ্যাসের আয়তন পরিমাপের একক।', 2),
                              ('LENGTH', 'bn', 'দৈর্ঘ্য', 'দূরত্ব বা দৈর্ঘ্য পরিমাপের একক।', 3),
                              ('AREA', 'bn', 'ক্ষেত্রফল', 'পৃষ্ঠের ক্ষেত্রফল পরিমাপের একক।', 4),
                              ('COUNT', 'bn', 'সংখ্যা', 'বিচ্ছিন্ন বস্তু গণনার একক।', 5),
                              ('TIME', 'bn', 'সময়', 'সময়কাল পরিমাপের একক।', 6),
                              ('TEMPERATURE', 'bn', 'তাপমাত্রা', 'তাপমাত্রা পরিমাপের একক।', 7),
                              ('ENERGY', 'bn', 'শক্তি', 'শক্তি বা ক্ষমতা পরিমাপের একক।', 8),
                              ('PRESSURE', 'bn', 'চাপ', 'চাপ পরিমাপের একক।', 9),
                              ('OTHER', 'bn', 'অন্যান্য', 'অন্যান্য বিবিধ এককের ধরন।',
                               10)) v(code, locale_code, name, description, sort_order)
                      ON ut.code = v.code
                 JOIN locales l ON l.code = v.locale_code
        ON CONFLICT (unit_type_id, locale_id) DO NOTHING;

    END
$$;


-- ============================================================
-- Audit history: append-only snapshot of every insert/update/
-- delete on unit_types, written by a trigger so it can't be
-- bypassed by any caller.
-- ============================================================

create table if not exists unit_type_history
(
    id           bigserial primary key,

    -- The unit_types row this history entry is a snapshot of. Not a FK: a
    -- history row must survive its parent row being hard-deleted.
    unit_type_id bigint                       not null,

    -- Which kind of change produced this snapshot.
    operation    varchar(10)                  not null check (operation in ('INSERT', 'UPDATE', 'DELETE')),

    -- Snapshot of unit_types.code at the time of the change.
    code         varchar(50),

    -- Snapshot of unit_types.sort_order at the time of the change.
    sort_order   integer,

    -- Snapshot of unit_types.is_active at the time of the change.
    is_active    boolean,

    -- Snapshot of unit_types.is_deleted at the time of the change.
    is_deleted   boolean,

    -- Who/when this history row was written — same person/time as the
    -- created_by/updated_by/deleted_by that triggered it, depending on operation.
    created_by   bigint references users (id) not null,
    created_at   timestamp with time zone     not null default current_timestamp,
    -- Kept for column-naming consistency with every other table; always equal
    -- to created_by/created_at since a history row is never actually updated.
    updated_by   bigint references users (id) not null,
    updated_at   timestamp with time zone     not null default current_timestamp,
    -- Kept for column-naming consistency; never increments, since updates are
    -- blocked entirely (see trg_unit_type_history_immutable below).
    version      bigint                       not null default 0,
    -- Kept for column-naming consistency; always null, since deletes are
    -- blocked entirely (see trg_unit_type_history_immutable below).
    deleted_by   bigint references users (id),
    deleted_at   timestamp with time zone
);

create index if not exists idx_unit_type_history_unit_type
    on unit_type_history (unit_type_id);

create or replace function trg_unit_types_audit()
    returns trigger as
$$
begin
    if (tg_op = 'INSERT') then
        insert into unit_type_history (unit_type_id, operation, code, sort_order, is_active, is_deleted,
                                        created_by, updated_by)
        values (new.id, 'INSERT', new.code, new.sort_order, new.is_active, new.is_deleted, new.created_by,
                new.created_by);
        return new;
    elsif (tg_op = 'UPDATE') then
        insert into unit_type_history (unit_type_id, operation, code, sort_order, is_active, is_deleted,
                                        created_by, updated_by)
        values (new.id, 'UPDATE', new.code, new.sort_order, new.is_active, new.is_deleted, new.updated_by,
                new.updated_by);
        return new;
    elsif (tg_op = 'DELETE') then
        insert into unit_type_history (unit_type_id, operation, code, sort_order, is_active, is_deleted,
                                        created_by, updated_by)
        values (old.id, 'DELETE', old.code, old.sort_order, old.is_active, old.is_deleted, old.deleted_by,
                old.deleted_by);
        return old;
    end if;
    return null;
end;
$$ language plpgsql;

drop trigger if exists trg_unit_types_audit on unit_types;
create trigger trg_unit_types_audit
    after insert or update or delete
    on unit_types
    for each row
execute function trg_unit_types_audit();

create or replace function fn_unit_type_history_immutable()
    returns trigger as
$$
begin
    raise exception 'unit_type_history rows are append-only and cannot be updated or deleted';
end;
$$ language plpgsql;

drop trigger if exists trg_unit_type_history_immutable on unit_type_history;
create trigger trg_unit_type_history_immutable
    before update or delete
    on unit_type_history
    for each row
execute function fn_unit_type_history_immutable();


-- ============================================================
-- Audit history: append-only snapshot of every insert/update/
-- delete on unit_type_locales, written by a trigger so it
-- can't be bypassed by any caller.
-- ============================================================

create table if not exists unit_type_locale_history
(
    id                  bigserial primary key,

    -- The unit_type_locales row this history entry is a snapshot of. Not
    -- a FK: a history row must survive its parent row being hard-deleted.
    unit_type_locale_id bigint                       not null,

    -- Which kind of change produced this snapshot.
    operation           varchar(10)                  not null check (operation in ('INSERT', 'UPDATE', 'DELETE')),

    -- Snapshot of unit_type_locales.unit_type_id at the time of the change.
    unit_type_id        bigint,

    -- Snapshot of unit_type_locales.locale_id at the time of the change.
    locale_id           bigint,

    -- Snapshot of unit_type_locales.name at the time of the change.
    name                varchar(100),

    -- Snapshot of unit_type_locales.description at the time of the change.
    description         text,

    -- Snapshot of unit_type_locales.sort_order at the time of the change.
    sort_order          integer,

    -- Snapshot of unit_type_locales.is_active at the time of the change.
    is_active           boolean,

    -- Snapshot of unit_type_locales.is_deleted at the time of the change.
    is_deleted           boolean,

    -- Who/when this history row was written — same person/time as the
    -- created_by/updated_by/deleted_by that triggered it, depending on operation.
    created_by          bigint references users (id) not null,
    created_at          timestamp with time zone     not null default current_timestamp,
    -- Kept for column-naming consistency with every other table; always equal
    -- to created_by/created_at since a history row is never actually updated.
    updated_by          bigint references users (id) not null,
    updated_at          timestamp with time zone     not null default current_timestamp,
    -- Kept for column-naming consistency; never increments, since updates are
    -- blocked entirely (see trg_unit_type_locale_history_immutable below).
    version             bigint                       not null default 0,
    -- Kept for column-naming consistency; always null, since deletes are
    -- blocked entirely (see trg_unit_type_locale_history_immutable below).
    deleted_by          bigint references users (id),
    deleted_at          timestamp with time zone
);

create index if not exists idx_unit_type_locale_history_unit_type_locale
    on unit_type_locale_history (unit_type_locale_id);

create or replace function trg_unit_type_locales_audit()
    returns trigger as
$$
begin
    if (tg_op = 'INSERT') then
        insert into unit_type_locale_history (unit_type_locale_id, operation, unit_type_id, locale_id, name,
                                               description, sort_order, is_active, is_deleted, created_by,
                                               updated_by)
        values (new.id, 'INSERT', new.unit_type_id, new.locale_id, new.name, new.description, new.sort_order,
                new.is_active, new.is_deleted, new.created_by, new.created_by);
        return new;
    elsif (tg_op = 'UPDATE') then
        insert into unit_type_locale_history (unit_type_locale_id, operation, unit_type_id, locale_id, name,
                                               description, sort_order, is_active, is_deleted, created_by,
                                               updated_by)
        values (new.id, 'UPDATE', new.unit_type_id, new.locale_id, new.name, new.description, new.sort_order,
                new.is_active, new.is_deleted, new.updated_by, new.updated_by);
        return new;
    elsif (tg_op = 'DELETE') then
        insert into unit_type_locale_history (unit_type_locale_id, operation, unit_type_id, locale_id, name,
                                               description, sort_order, is_active, is_deleted, created_by,
                                               updated_by)
        values (old.id, 'DELETE', old.unit_type_id, old.locale_id, old.name, old.description, old.sort_order,
                old.is_active, old.is_deleted, old.deleted_by, old.deleted_by);
        return old;
    end if;
    return null;
end;
$$ language plpgsql;

drop trigger if exists trg_unit_type_locales_audit on unit_type_locales;
create trigger trg_unit_type_locales_audit
    after insert or update or delete
    on unit_type_locales
    for each row
execute function trg_unit_type_locales_audit();

create or replace function fn_unit_type_locale_history_immutable()
    returns trigger as
$$
begin
    raise exception 'unit_type_locale_history rows are append-only and cannot be updated or deleted';
end;
$$ language plpgsql;

drop trigger if exists trg_unit_type_locale_history_immutable on unit_type_locale_history;
create trigger trg_unit_type_locale_history_immutable
    before update or delete
    on unit_type_locale_history
    for each row
execute function fn_unit_type_locale_history_immutable();

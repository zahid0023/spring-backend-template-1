create table if not exists currencies
(
    id             bigserial primary key,

    country_id     bigint references countries (id) not null,

    -- ISO 4217 alphabetic code.
    -- Examples:
    -- BDT
    -- USD
    -- EUR
    -- GBP
    -- INR
    code           char(3)                          not null unique,
    sort_order     integer                          not null default 0,

    -- ISO 4217 numeric code.
    -- Examples:
    -- 050
    -- 840
    -- 978
    numeric_code   char(3)                          not null unique,

    -- Currency symbol.
    -- Examples:
    -- ৳
    -- $
    -- €
    -- £
    symbol         varchar(10)                      not null,

    -- Number of decimal places.
    -- Examples:
    -- 2 for USD
    -- 0 for JPY
    decimal_places smallint                         not null default 2,

    -- Whether this is the platform default currency.
    is_default     boolean                          not null default false,

    created_by     bigint references users (id)     not null,
    created_at     timestamp with time zone         not null default current_timestamp,
    updated_by     bigint references users (id)     not null,
    updated_at     timestamp with time zone         not null default current_timestamp,
    version        bigint                           not null default 0,
    is_active      boolean                          not null default true,
    is_deleted     boolean                          not null default false,
    deleted_by     bigint references users (id),
    deleted_at     timestamp with time zone
);

-- Only one active, non-deleted currency can be the platform default.
create unique index if not exists uq_currencies_single_default
    on currencies (is_default)
    where is_default = true
        and is_active = true
        and is_deleted = false;

create table if not exists currency_locales
(
    id          bigserial primary key,

    currency_id bigint references currencies (id) on delete cascade not null,
    locale_id   bigint references locales (id)                      not null,

    -- Examples:
    -- Bangladeshi Taka
    -- US Dollar
    -- Euro
    -- British Pound Sterling
    name        varchar(200)                                        not null,
    -- Optional short name.
    -- Examples:
    -- Taka
    -- Dollar
    -- Euro
    short_name  varchar(100),
    sort_order  integer                                             not null default 0,

    created_by  bigint references users (id)                        not null,
    created_at  timestamp with time zone                            not null default current_timestamp,
    updated_by  bigint references users (id)                        not null,
    updated_at  timestamp with time zone                            not null default current_timestamp,
    version     bigint                                              not null default 0,
    is_active   boolean                                             not null default true,
    is_deleted  boolean                                             not null default false,
    deleted_by  bigint references users (id),
    deleted_at  timestamp with time zone,

    constraint uq_currency_locale
        unique (currency_id, locale_id)
);

DO
$$
    DECLARE
        sys_id bigint;
    BEGIN
        SELECT id INTO sys_id FROM users WHERE username = 'system';

        -- =============================================
        -- 1. Currencies
        -- =============================================
        INSERT INTO currencies (code, numeric_code, symbol, decimal_places, is_default, sort_order, country_id,
                                created_by, updated_by)
        SELECT v.code,
               v.numeric_code,
               v.symbol,
               v.decimal_places,
               v.is_default,
               v.sort_order,
               c.id,
               sys_id,
               sys_id
        FROM (VALUES ('BDT', '050', '৳', 2, true, 1, 'BD'),
                     ('USD', '840', '$', 2, false, 2, 'US')) v(code, numeric_code, symbol, decimal_places, is_default, sort_order, country_code)
                 JOIN countries c ON c.code = v.country_code
        ON CONFLICT (code) DO NOTHING;

        -- =============================================
        -- 2. Currency Locales
        -- =============================================
        INSERT INTO currency_locales (currency_id, locale_id, name, short_name, sort_order, created_by, updated_by)
        SELECT cu.id, l.id, v.name, v.short_name, v.sort_order, sys_id, sys_id
        FROM currencies cu
                 JOIN (VALUES ('BDT', 'en', 'Bangladeshi Taka', 'Taka', 1),
                              ('BDT', 'bn', 'বাংলাদেশী টাকা', 'টাকা', 2),
                              ('USD', 'en', 'US Dollar', 'Dollar', 1),
                              ('USD', 'bn', 'মার্কিন ডলার', 'ডলার', 2)) v(code, locale_code, name, short_name, sort_order)
                      ON cu.code = v.code
                 JOIN locales l ON l.code = v.locale_code
        ON CONFLICT (currency_id, locale_id) DO NOTHING;

    END
$$;


-- ============================================================
-- Audit history: append-only snapshot of every insert/update/
-- delete on currencies, written by a trigger so it can't be
-- bypassed by any caller.
-- ============================================================

create table if not exists currency_history
(
    id             bigserial primary key,

    -- The currencies row this history entry is a snapshot of. Not a FK: a
    -- history row must survive its parent row being hard-deleted.
    currency_id    bigint                       not null,

    -- Which kind of change produced this snapshot.
    operation      varchar(10)                  not null check (operation in ('INSERT', 'UPDATE', 'DELETE')),

    -- Snapshot of currencies.country_id at the time of the change.
    country_id     bigint,

    -- Snapshot of currencies.code at the time of the change.
    code           char(3),

    -- Snapshot of currencies.sort_order at the time of the change.
    sort_order     integer,

    -- Snapshot of currencies.numeric_code at the time of the change.
    numeric_code   char(3),

    -- Snapshot of currencies.symbol at the time of the change.
    symbol         varchar(10),

    -- Snapshot of currencies.decimal_places at the time of the change.
    decimal_places smallint,

    -- Snapshot of currencies.is_default at the time of the change.
    is_default     boolean,

    -- Snapshot of currencies.is_active at the time of the change.
    is_active      boolean,

    -- Snapshot of currencies.is_deleted at the time of the change.
    is_deleted     boolean,

    -- Who/when this history row was written — same person/time as the
    -- created_by/updated_by/deleted_by that triggered it, depending on operation.
    created_by     bigint references users (id) not null,
    created_at     timestamp with time zone     not null default current_timestamp,
    -- Kept for column-naming consistency with every other table; always equal
    -- to created_by/created_at since a history row is never actually updated.
    updated_by     bigint references users (id) not null,
    updated_at     timestamp with time zone     not null default current_timestamp,
    -- Kept for column-naming consistency; never increments, since updates are
    -- blocked entirely (see trg_currency_history_immutable below).
    version        bigint                       not null default 0,
    -- Kept for column-naming consistency; always null, since deletes are
    -- blocked entirely (see trg_currency_history_immutable below).
    deleted_by     bigint references users (id),
    deleted_at     timestamp with time zone
);

create index if not exists idx_currency_history_currency
    on currency_history (currency_id);

create or replace function trg_currencies_audit()
    returns trigger as
$$
begin
    if (tg_op = 'INSERT') then
        insert into currency_history (currency_id, operation, country_id, code, sort_order, numeric_code, symbol,
                                       decimal_places, is_default, is_active, is_deleted, created_by, updated_by)
        values (new.id, 'INSERT', new.country_id, new.code, new.sort_order, new.numeric_code, new.symbol,
                new.decimal_places, new.is_default, new.is_active, new.is_deleted, new.created_by, new.created_by);
        return new;
    elsif (tg_op = 'UPDATE') then
        insert into currency_history (currency_id, operation, country_id, code, sort_order, numeric_code, symbol,
                                       decimal_places, is_default, is_active, is_deleted, created_by, updated_by)
        values (new.id, 'UPDATE', new.country_id, new.code, new.sort_order, new.numeric_code, new.symbol,
                new.decimal_places, new.is_default, new.is_active, new.is_deleted, new.updated_by, new.updated_by);
        return new;
    elsif (tg_op = 'DELETE') then
        insert into currency_history (currency_id, operation, country_id, code, sort_order, numeric_code, symbol,
                                       decimal_places, is_default, is_active, is_deleted, created_by, updated_by)
        values (old.id, 'DELETE', old.country_id, old.code, old.sort_order, old.numeric_code, old.symbol,
                old.decimal_places, old.is_default, old.is_active, old.is_deleted, old.deleted_by, old.deleted_by);
        return old;
    end if;
    return null;
end;
$$ language plpgsql;

drop trigger if exists trg_currencies_audit on currencies;
create trigger trg_currencies_audit
    after insert or update or delete
    on currencies
    for each row
execute function trg_currencies_audit();

create or replace function fn_currency_history_immutable()
    returns trigger as
$$
begin
    raise exception 'currency_history rows are append-only and cannot be updated or deleted';
end;
$$ language plpgsql;

drop trigger if exists trg_currency_history_immutable on currency_history;
create trigger trg_currency_history_immutable
    before update or delete
    on currency_history
    for each row
execute function fn_currency_history_immutable();


-- ============================================================
-- Audit history: append-only snapshot of every insert/update/
-- delete on currency_locales, written by a trigger so it can't
-- be bypassed by any caller.
-- ============================================================

create table if not exists currency_locale_history
(
    id                 bigserial primary key,

    -- The currency_locales row this history entry is a snapshot of. Not a
    -- FK: a history row must survive its parent row being hard-deleted.
    currency_locale_id bigint                       not null,

    -- Which kind of change produced this snapshot.
    operation          varchar(10)                  not null check (operation in ('INSERT', 'UPDATE', 'DELETE')),

    -- Snapshot of currency_locales.currency_id at the time of the change.
    currency_id        bigint,

    -- Snapshot of currency_locales.locale_id at the time of the change.
    locale_id          bigint,

    -- Snapshot of currency_locales.name at the time of the change.
    name               varchar(200),

    -- Snapshot of currency_locales.short_name at the time of the change.
    short_name         varchar(100),

    -- Snapshot of currency_locales.sort_order at the time of the change.
    sort_order         integer,

    -- Snapshot of currency_locales.is_active at the time of the change.
    is_active          boolean,

    -- Snapshot of currency_locales.is_deleted at the time of the change.
    is_deleted         boolean,

    -- Who/when this history row was written — same person/time as the
    -- created_by/updated_by/deleted_by that triggered it, depending on operation.
    created_by         bigint references users (id) not null,
    created_at         timestamp with time zone     not null default current_timestamp,
    -- Kept for column-naming consistency with every other table; always equal
    -- to created_by/created_at since a history row is never actually updated.
    updated_by         bigint references users (id) not null,
    updated_at         timestamp with time zone     not null default current_timestamp,
    -- Kept for column-naming consistency; never increments, since updates are
    -- blocked entirely (see trg_currency_locale_history_immutable below).
    version            bigint                       not null default 0,
    -- Kept for column-naming consistency; always null, since deletes are
    -- blocked entirely (see trg_currency_locale_history_immutable below).
    deleted_by         bigint references users (id),
    deleted_at         timestamp with time zone
);

create index if not exists idx_currency_locale_history_currency_locale
    on currency_locale_history (currency_locale_id);

create or replace function trg_currency_locales_audit()
    returns trigger as
$$
begin
    if (tg_op = 'INSERT') then
        insert into currency_locale_history (currency_locale_id, operation, currency_id, locale_id, name,
                                              short_name, sort_order, is_active, is_deleted, created_by, updated_by)
        values (new.id, 'INSERT', new.currency_id, new.locale_id, new.name, new.short_name, new.sort_order,
                new.is_active, new.is_deleted, new.created_by, new.created_by);
        return new;
    elsif (tg_op = 'UPDATE') then
        insert into currency_locale_history (currency_locale_id, operation, currency_id, locale_id, name,
                                              short_name, sort_order, is_active, is_deleted, created_by, updated_by)
        values (new.id, 'UPDATE', new.currency_id, new.locale_id, new.name, new.short_name, new.sort_order,
                new.is_active, new.is_deleted, new.updated_by, new.updated_by);
        return new;
    elsif (tg_op = 'DELETE') then
        insert into currency_locale_history (currency_locale_id, operation, currency_id, locale_id, name,
                                              short_name, sort_order, is_active, is_deleted, created_by, updated_by)
        values (old.id, 'DELETE', old.currency_id, old.locale_id, old.name, old.short_name, old.sort_order,
                old.is_active, old.is_deleted, old.deleted_by, old.deleted_by);
        return old;
    end if;
    return null;
end;
$$ language plpgsql;

drop trigger if exists trg_currency_locales_audit on currency_locales;
create trigger trg_currency_locales_audit
    after insert or update or delete
    on currency_locales
    for each row
execute function trg_currency_locales_audit();

create or replace function fn_currency_locale_history_immutable()
    returns trigger as
$$
begin
    raise exception 'currency_locale_history rows are append-only and cannot be updated or deleted';
end;
$$ language plpgsql;

drop trigger if exists trg_currency_locale_history_immutable on currency_locale_history;
create trigger trg_currency_locale_history_immutable
    before update or delete
    on currency_locale_history
    for each row
execute function fn_currency_locale_history_immutable();

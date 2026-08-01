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
        FROM (VALUES ('BDT', '050', '৳', 2, true, 1)) v(code, numeric_code, symbol, decimal_places, is_default, sort_order)
                 JOIN countries c ON c.code = 'BD'
        ON CONFLICT (code) DO NOTHING;

        -- =============================================
        -- 2. Currency Locales (English)
        -- =============================================
        INSERT INTO currency_locales (currency_id, locale_id, name, short_name, sort_order, created_by, updated_by)
        SELECT cu.id, l.id, v.name, v.short_name, v.sort_order, sys_id, sys_id
        FROM currencies cu
                 JOIN (VALUES ('BDT', 'en', 'Bangladeshi Taka', 'Taka', 1)) v(code, locale_code, name, short_name, sort_order)
                      ON cu.code = v.code
                 JOIN locales l ON l.code = v.locale_code
        ON CONFLICT (currency_id, locale_id) DO NOTHING;

    END
$$;

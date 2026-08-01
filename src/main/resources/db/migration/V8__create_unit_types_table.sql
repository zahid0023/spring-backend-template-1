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

    END
$$;

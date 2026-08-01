create table if not exists units
(
    id                bigserial primary key,

    -- Parent unit type.
    unit_type_id      bigint references unit_types (id) not null,

    -- KG
    -- G
    -- L
    -- ML
    -- M
    -- CM
    -- PIECE
    code              varchar(50)                       not null unique,

    -- kg
    -- g
    -- L
    -- m
    -- pcs
    symbol            varchar(20)                       not null unique,

    -- Whether this is the base unit of its type.
    -- Example:
    -- WEIGHT -> G
    -- VOLUME -> ML
    -- LENGTH -> MM
    -- COUNT -> PIECE
    is_base_unit      boolean                           not null default false,

    -- Number of base units represented by one unit.
    --
    -- Examples:
    -- G      = 1
    -- KG     = 1000
    -- TON    = 1000000
    -- ML     = 1
    -- L      = 1000
    -- CM     = 10
    -- M      = 1000
    conversion_factor numeric(20, 8)                    not null default 1,

    sort_order        integer                           not null default 0,

    created_by        bigint references users (id)      not null,
    created_at        timestamp with time zone          not null default current_timestamp,
    updated_by        bigint references users (id)      not null,
    updated_at        timestamp with time zone          not null default current_timestamp,
    version           bigint                            not null default 0,
    is_active         boolean                           not null default true,
    is_deleted        boolean                           not null default false,
    deleted_by        bigint references users (id),
    deleted_at        timestamp with time zone,

    constraint chk_conversion_factor
        check (conversion_factor > 0)
);

create table if not exists unit_locales
(
    id          bigserial primary key,

    unit_id     bigint references units (id) on delete cascade not null,
    locale_id   bigint references locales (id)                 not null,

    -- Kilogram
    -- Gram
    -- Liter
    name        varchar(100)                                   not null,
    -- Kilograms
    -- Grams
    plural_name varchar(100)                                   not null,
    description text                                           not null default '',
    sort_order  integer                                        not null default 0,

    created_by  bigint references users (id)                   not null,
    created_at  timestamp with time zone                       not null default current_timestamp,
    updated_by  bigint references users (id)                   not null,
    updated_at  timestamp with time zone                       not null default current_timestamp,
    version     bigint                                         not null default 0,
    is_active   boolean                                        not null default true,
    is_deleted  boolean                                        not null default false,
    deleted_by  bigint references users (id),
    deleted_at  timestamp with time zone,

    constraint uq_unit_locale
        unique (unit_id, locale_id)
);

DO
$$
    DECLARE
        sys_id bigint;
    BEGIN
        SELECT id INTO sys_id FROM users WHERE username = 'system';

        -- =============================================
        -- 1. Units
        -- =============================================
        INSERT INTO units (unit_type_id, code, symbol, is_base_unit, conversion_factor, sort_order, created_by,
                           updated_by)
        SELECT ut.id,
               v.code,
               v.symbol,
               v.is_base_unit,
               v.conversion_factor,
               v.sort_order,
               sys_id,
               sys_id
        FROM (VALUES
                  -- WEIGHT  (base = G)
                  ('WEIGHT', 'G', 'g', true, 1.0, 1),
                  ('WEIGHT', 'KG', 'kg', false, 1000.0, 2),
                  ('WEIGHT', 'TON', 't', false, 1000000.0, 3),
                  -- VOLUME  (base = ML)
                  ('VOLUME', 'ML', 'mL', true, 1.0, 1),
                  ('VOLUME', 'L', 'L', false, 1000.0, 2),
                  -- LENGTH  (base = MM)
                  ('LENGTH', 'MM', 'mm', true, 1.0, 1),
                  ('LENGTH', 'CM', 'cm', false, 10.0, 2),
                  ('LENGTH', 'M', 'm', false, 1000.0, 3),
                  ('LENGTH', 'KM', 'km', false, 1000000.0, 4),
                  -- AREA    (base = SQ_M)
                  ('AREA', 'SQ_CM', 'cm²', false, 0.0001, 1),
                  ('AREA', 'SQ_M', 'm²', true, 1.0, 2),
                  ('AREA', 'SQ_KM', 'km²', false, 1000000.0, 3),
                  -- COUNT   (base = PIECE)
                  ('COUNT', 'PIECE', 'pc', true, 1.0, 1),
                  ('COUNT', 'DOZEN', 'doz', false, 12.0, 2),
                  -- TIME    (base = SEC)
                  ('TIME', 'SEC', 's', true, 1.0, 1),
                  ('TIME', 'MIN', 'min', false, 60.0, 2),
                  ('TIME', 'HR', 'h', false, 3600.0, 3),
                  ('TIME', 'DAY', 'd', false, 86400.0, 4),
                  -- TEMPERATURE  (non-linear; conversion_factor = 1 for all)
                  ('TEMPERATURE', 'CEL', '°C', true, 1.0, 1),
                  ('TEMPERATURE', 'FAH', '°F', false, 1.0, 2),
                  ('TEMPERATURE', 'KEL', 'K', false, 1.0, 3),
                  -- ENERGY  (base = J)
                  ('ENERGY', 'J', 'J', true, 1.0, 1),
                  ('ENERGY', 'KJ', 'kJ', false, 1000.0, 2),
                  ('ENERGY', 'CAL', 'cal', false, 4.184, 3),
                  ('ENERGY', 'KCAL', 'kcal', false, 4184.0, 4),
                  -- PRESSURE (base = PA)
                  ('PRESSURE', 'PA', 'Pa', true, 1.0, 1),
                  ('PRESSURE', 'KPA', 'kPa', false, 1000.0, 2),
                  ('PRESSURE', 'BAR', 'bar', false, 100000.0, 3),
                  ('PRESSURE', 'PSI', 'psi', false, 6894.757, 4),
                  -- OTHER
                  ('OTHER', 'UNIT', 'unit', true, 1.0,
                   1)) v(type_code, code, symbol, is_base_unit, conversion_factor, sort_order)
                 JOIN unit_types ut ON ut.code = v.type_code
        ON CONFLICT (code) DO NOTHING;

        -- =============================================
        -- 2. Unit Locales (English)
        -- =============================================
        INSERT INTO unit_locales (unit_id, locale_id, name, plural_name, description, sort_order, created_by,
                                  updated_by)
        SELECT u.id,
               l.id,
               v.name,
               v.plural_name,
               v.description,
               v.sort_order,
               sys_id,
               sys_id
        FROM units u
                 JOIN (VALUES ('G', 'en', 'Gram', 'Grams', 'Base unit of weight.', 1),
                              ('KG', 'en', 'Kilogram', 'Kilograms', 'Equal to 1,000 grams.', 1),
                              ('TON', 'en', 'Metric Ton', 'Metric Tons', 'Equal to 1,000 kilograms.', 1),
                              ('ML', 'en', 'Milliliter', 'Milliliters', 'Base unit of volume.', 1),
                              ('L', 'en', 'Liter', 'Liters', 'Equal to 1,000 milliliters.', 1),
                              ('MM', 'en', 'Millimeter', 'Millimeters', 'Base unit of length.', 1),
                              ('CM', 'en', 'Centimeter', 'Centimeters', 'Equal to 10 millimeters.', 1),
                              ('M', 'en', 'Meter', 'Meters', 'Equal to 1,000 millimeters.', 1),
                              ('KM', 'en', 'Kilometer', 'Kilometers', 'Equal to 1,000,000 millimeters.', 1),
                              ('SQ_CM', 'en', 'Square Centimeter', 'Square Centimeters',
                               'Equal to 0.0001 square meters.', 1),
                              ('SQ_M', 'en', 'Square Meter', 'Square Meters', 'Base unit of area.', 1),
                              ('SQ_KM', 'en', 'Square Kilometer', 'Square Kilometers',
                               'Equal to 1,000,000 square meters.', 1),
                              ('PIECE', 'en', 'Piece', 'Pieces', 'Base unit of count.', 1),
                              ('DOZEN', 'en', 'Dozen', 'Dozens', 'Equal to 12 pieces.', 1),
                              ('SEC', 'en', 'Second', 'Seconds', 'Base unit of time.', 1),
                              ('MIN', 'en', 'Minute', 'Minutes', 'Equal to 60 seconds.', 1),
                              ('HR', 'en', 'Hour', 'Hours', 'Equal to 3,600 seconds.', 1),
                              ('DAY', 'en', 'Day', 'Days', 'Equal to 86,400 seconds.', 1),
                              ('CEL', 'en', 'Degree Celsius', 'Degrees Celsius', 'Base unit of temperature.', 1),
                              ('FAH', 'en', 'Degree Fahrenheit', 'Degrees Fahrenheit',
                               'Non-linear conversion from Celsius.', 1),
                              ('KEL', 'en', 'Kelvin', 'Kelvins', 'Absolute temperature scale.', 1),
                              ('J', 'en', 'Joule', 'Joules', 'Base unit of energy.', 1),
                              ('KJ', 'en', 'Kilojoule', 'Kilojoules', 'Equal to 1,000 joules.', 1),
                              ('CAL', 'en', 'Calorie', 'Calories', 'Equal to approximately 4.184 joules.', 1),
                              ('KCAL', 'en', 'Kilocalorie', 'Kilocalories', 'Equal to approximately 4,184 joules.', 1),
                              ('PA', 'en', 'Pascal', 'Pascals', 'Base unit of pressure.', 1),
                              ('KPA', 'en', 'Kilopascal', 'Kilopascals', 'Equal to 1,000 pascals.', 1),
                              ('BAR', 'en', 'Bar', 'Bars', 'Equal to 100,000 pascals.', 1),
                              ('PSI', 'en', 'Pound per Square Inch', 'Pounds per Square Inch',
                               'Equal to approximately 6,894.757 pascals.', 1),
                              ('UNIT', 'en', 'Unit', 'Units', 'Generic unit for miscellaneous quantities.',
                               1)) v(code, locale_code, name, plural_name, description, sort_order)
                      ON u.code = v.code
                 JOIN locales l ON l.code = v.locale_code
        ON CONFLICT (unit_id, locale_id) DO NOTHING;

    END
$$;

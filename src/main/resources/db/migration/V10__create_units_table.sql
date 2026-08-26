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

        -- =============================================
        -- 3. Unit Locales (Bengali)
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
                 JOIN (VALUES ('G', 'bn', 'গ্রাম', 'গ্রামসমূহ', 'ওজনের মৌলিক একক।', 2),
                              ('KG', 'bn', 'কিলোগ্রাম', 'কিলোগ্রামসমূহ', '১,০০০ গ্রামের সমান।', 2),
                              ('TON', 'bn', 'মেট্রিক টন', 'মেট্রিক টনসমূহ', '১,০০০ কিলোগ্রামের সমান।', 2),
                              ('ML', 'bn', 'মিলিলিটার', 'মিলিলিটারসমূহ', 'আয়তনের মৌলিক একক।', 2),
                              ('L', 'bn', 'লিটার', 'লিটারসমূহ', '১,০০০ মিলিলিটারের সমান।', 2),
                              ('MM', 'bn', 'মিলিমিটার', 'মিলিমিটারসমূহ', 'দৈর্ঘ্যের মৌলিক একক।', 2),
                              ('CM', 'bn', 'সেন্টিমিটার', 'সেন্টিমিটারসমূহ', '১০ মিলিমিটারের সমান।', 2),
                              ('M', 'bn', 'মিটার', 'মিটারসমূহ', '১,০০০ মিলিমিটারের সমান।', 2),
                              ('KM', 'bn', 'কিলোমিটার', 'কিলোমিটারসমূহ', '১,০০০,০০০ মিলিমিটারের সমান।', 2),
                              ('SQ_CM', 'bn', 'বর্গ সেন্টিমিটার', 'বর্গ সেন্টিমিটারসমূহ',
                               '০.০০০১ বর্গ মিটারের সমান।', 2),
                              ('SQ_M', 'bn', 'বর্গ মিটার', 'বর্গ মিটারসমূহ', 'ক্ষেত্রফলের মৌলিক একক।', 2),
                              ('SQ_KM', 'bn', 'বর্গ কিলোমিটার', 'বর্গ কিলোমিটারসমূহ',
                               '১,০০০,০০০ বর্গ মিটারের সমান।', 2),
                              ('PIECE', 'bn', 'টুকরা', 'টুকরাসমূহ', 'গণনার মৌলিক একক।', 2),
                              ('DOZEN', 'bn', 'ডজন', 'ডজনসমূহ', '১২ টুকরার সমান।', 2),
                              ('SEC', 'bn', 'সেকেন্ড', 'সেকেন্ডসমূহ', 'সময়ের মৌলিক একক।', 2),
                              ('MIN', 'bn', 'মিনিট', 'মিনিটসমূহ', '৬০ সেকেন্ডের সমান।', 2),
                              ('HR', 'bn', 'ঘণ্টা', 'ঘণ্টাসমূহ', '৩,৬০০ সেকেন্ডের সমান।', 2),
                              ('DAY', 'bn', 'দিন', 'দিনসমূহ', '৮৬,৪০০ সেকেন্ডের সমান।', 2),
                              ('CEL', 'bn', 'ডিগ্রি সেলসিয়াস', 'ডিগ্রি সেলসিয়াসসমূহ', 'তাপমাত্রার মৌলিক একক।', 2),
                              ('FAH', 'bn', 'ডিগ্রি ফারেনহাইট', 'ডিগ্রি ফারেনহাইটসমূহ',
                               'সেলসিয়াস থেকে অরৈখিক রূপান্তর।', 2),
                              ('KEL', 'bn', 'কেলভিন', 'কেলভিনসমূহ', 'পরম তাপমাত্রা স্কেল।', 2),
                              ('J', 'bn', 'জুল', 'জুলসমূহ', 'শক্তির মৌলিক একক।', 2),
                              ('KJ', 'bn', 'কিলোজুল', 'কিলোজুলসমূহ', '১,০০০ জুলের সমান।', 2),
                              ('CAL', 'bn', 'ক্যালোরি', 'ক্যালোরিসমূহ', 'প্রায় ৪.১৮৪ জুলের সমান।', 2),
                              ('KCAL', 'bn', 'কিলোক্যালোরি', 'কিলোক্যালোরিসমূহ', 'প্রায় ৪,১৮৪ জুলের সমান।', 2),
                              ('PA', 'bn', 'প্যাসকেল', 'প্যাসকেলসমূহ', 'চাপের মৌলিক একক।', 2),
                              ('KPA', 'bn', 'কিলোপ্যাসকেল', 'কিলোপ্যাসকেলসমূহ', '১,০০০ প্যাসকেলের সমান।', 2),
                              ('BAR', 'bn', 'বার', 'বারসমূহ', '১,০০,০০০ প্যাসকেলের সমান।', 2),
                              ('PSI', 'bn', 'পাউন্ড প্রতি বর্গ ইঞ্চি', 'পাউন্ড প্রতি বর্গ ইঞ্চিসমূহ',
                               'প্রায় ৬,৮৯৪.৭৫৭ প্যাসকেলের সমান।', 2),
                              ('UNIT', 'bn', 'একক', 'এককসমূহ', 'বিবিধ পরিমাণের জন্য সাধারণ একক।',
                               2)) v(code, locale_code, name, plural_name, description, sort_order)
                      ON u.code = v.code
                 JOIN locales l ON l.code = v.locale_code
        ON CONFLICT (unit_id, locale_id) DO NOTHING;

    END
$$;


-- ============================================================
-- Audit history: append-only snapshot of every insert/update/
-- delete on units, written by a trigger so it can't be
-- bypassed by any caller.
-- ============================================================

create table if not exists unit_history
(
    id                bigserial primary key,

    -- The units row this history entry is a snapshot of. Not a FK: a
    -- history row must survive its parent row being hard-deleted.
    unit_id           bigint                       not null,

    -- Which kind of change produced this snapshot.
    operation         varchar(10)                  not null check (operation in ('INSERT', 'UPDATE', 'DELETE')),

    -- Snapshot of units.unit_type_id at the time of the change.
    unit_type_id      bigint,

    -- Snapshot of units.code at the time of the change.
    code              varchar(50),

    -- Snapshot of units.symbol at the time of the change.
    symbol            varchar(20),

    -- Snapshot of units.is_base_unit at the time of the change.
    is_base_unit      boolean,

    -- Snapshot of units.conversion_factor at the time of the change.
    conversion_factor numeric(20, 8),

    -- Snapshot of units.sort_order at the time of the change.
    sort_order        integer,

    -- Snapshot of units.is_active at the time of the change.
    is_active         boolean,

    -- Snapshot of units.is_deleted at the time of the change.
    is_deleted        boolean,

    -- Who/when this history row was written — same person/time as the
    -- created_by/updated_by/deleted_by that triggered it, depending on operation.
    created_by        bigint references users (id) not null,
    created_at        timestamp with time zone     not null default current_timestamp,
    -- Kept for column-naming consistency with every other table; always equal
    -- to created_by/created_at since a history row is never actually updated.
    updated_by        bigint references users (id) not null,
    updated_at        timestamp with time zone     not null default current_timestamp,
    -- Kept for column-naming consistency; never increments, since updates are
    -- blocked entirely (see trg_unit_history_immutable below).
    version           bigint                       not null default 0,
    -- Kept for column-naming consistency; always null, since deletes are
    -- blocked entirely (see trg_unit_history_immutable below).
    deleted_by        bigint references users (id),
    deleted_at        timestamp with time zone
);

create index if not exists idx_unit_history_unit
    on unit_history (unit_id);

create or replace function trg_units_audit()
    returns trigger as
$$
begin
    if (tg_op = 'INSERT') then
        insert into unit_history (unit_id, operation, unit_type_id, code, symbol, is_base_unit, conversion_factor,
                                   sort_order, is_active, is_deleted, created_by, updated_by)
        values (new.id, 'INSERT', new.unit_type_id, new.code, new.symbol, new.is_base_unit,
                new.conversion_factor, new.sort_order, new.is_active, new.is_deleted, new.created_by,
                new.created_by);
        return new;
    elsif (tg_op = 'UPDATE') then
        insert into unit_history (unit_id, operation, unit_type_id, code, symbol, is_base_unit, conversion_factor,
                                   sort_order, is_active, is_deleted, created_by, updated_by)
        values (new.id, 'UPDATE', new.unit_type_id, new.code, new.symbol, new.is_base_unit,
                new.conversion_factor, new.sort_order, new.is_active, new.is_deleted, new.updated_by,
                new.updated_by);
        return new;
    elsif (tg_op = 'DELETE') then
        insert into unit_history (unit_id, operation, unit_type_id, code, symbol, is_base_unit, conversion_factor,
                                   sort_order, is_active, is_deleted, created_by, updated_by)
        values (old.id, 'DELETE', old.unit_type_id, old.code, old.symbol, old.is_base_unit,
                old.conversion_factor, old.sort_order, old.is_active, old.is_deleted, old.deleted_by,
                old.deleted_by);
        return old;
    end if;
    return null;
end;
$$ language plpgsql;

drop trigger if exists trg_units_audit on units;
create trigger trg_units_audit
    after insert or update or delete
    on units
    for each row
execute function trg_units_audit();

create or replace function fn_unit_history_immutable()
    returns trigger as
$$
begin
    raise exception 'unit_history rows are append-only and cannot be updated or deleted';
end;
$$ language plpgsql;

drop trigger if exists trg_unit_history_immutable on unit_history;
create trigger trg_unit_history_immutable
    before update or delete
    on unit_history
    for each row
execute function fn_unit_history_immutable();


-- ============================================================
-- Audit history: append-only snapshot of every insert/update/
-- delete on unit_locales, written by a trigger so it can't be
-- bypassed by any caller.
-- ============================================================

create table if not exists unit_locale_history
(
    id              bigserial primary key,

    -- The unit_locales row this history entry is a snapshot of. Not a
    -- FK: a history row must survive its parent row being hard-deleted.
    unit_locale_id  bigint                       not null,

    -- Which kind of change produced this snapshot.
    operation       varchar(10)                  not null check (operation in ('INSERT', 'UPDATE', 'DELETE')),

    -- Snapshot of unit_locales.unit_id at the time of the change.
    unit_id         bigint,

    -- Snapshot of unit_locales.locale_id at the time of the change.
    locale_id       bigint,

    -- Snapshot of unit_locales.name at the time of the change.
    name            varchar(100),

    -- Snapshot of unit_locales.plural_name at the time of the change.
    plural_name     varchar(100),

    -- Snapshot of unit_locales.description at the time of the change.
    description     text,

    -- Snapshot of unit_locales.sort_order at the time of the change.
    sort_order      integer,

    -- Snapshot of unit_locales.is_active at the time of the change.
    is_active       boolean,

    -- Snapshot of unit_locales.is_deleted at the time of the change.
    is_deleted      boolean,

    -- Who/when this history row was written — same person/time as the
    -- created_by/updated_by/deleted_by that triggered it, depending on operation.
    created_by      bigint references users (id) not null,
    created_at      timestamp with time zone     not null default current_timestamp,
    -- Kept for column-naming consistency with every other table; always equal
    -- to created_by/created_at since a history row is never actually updated.
    updated_by      bigint references users (id) not null,
    updated_at      timestamp with time zone     not null default current_timestamp,
    -- Kept for column-naming consistency; never increments, since updates are
    -- blocked entirely (see trg_unit_locale_history_immutable below).
    version         bigint                       not null default 0,
    -- Kept for column-naming consistency; always null, since deletes are
    -- blocked entirely (see trg_unit_locale_history_immutable below).
    deleted_by      bigint references users (id),
    deleted_at      timestamp with time zone
);

create index if not exists idx_unit_locale_history_unit_locale
    on unit_locale_history (unit_locale_id);

create or replace function trg_unit_locales_audit()
    returns trigger as
$$
begin
    if (tg_op = 'INSERT') then
        insert into unit_locale_history (unit_locale_id, operation, unit_id, locale_id, name, plural_name,
                                          description, sort_order, is_active, is_deleted, created_by, updated_by)
        values (new.id, 'INSERT', new.unit_id, new.locale_id, new.name, new.plural_name, new.description,
                new.sort_order, new.is_active, new.is_deleted, new.created_by, new.created_by);
        return new;
    elsif (tg_op = 'UPDATE') then
        insert into unit_locale_history (unit_locale_id, operation, unit_id, locale_id, name, plural_name,
                                          description, sort_order, is_active, is_deleted, created_by, updated_by)
        values (new.id, 'UPDATE', new.unit_id, new.locale_id, new.name, new.plural_name, new.description,
                new.sort_order, new.is_active, new.is_deleted, new.updated_by, new.updated_by);
        return new;
    elsif (tg_op = 'DELETE') then
        insert into unit_locale_history (unit_locale_id, operation, unit_id, locale_id, name, plural_name,
                                          description, sort_order, is_active, is_deleted, created_by, updated_by)
        values (old.id, 'DELETE', old.unit_id, old.locale_id, old.name, old.plural_name, old.description,
                old.sort_order, old.is_active, old.is_deleted, old.deleted_by, old.deleted_by);
        return old;
    end if;
    return null;
end;
$$ language plpgsql;

drop trigger if exists trg_unit_locales_audit on unit_locales;
create trigger trg_unit_locales_audit
    after insert or update or delete
    on unit_locales
    for each row
execute function trg_unit_locales_audit();

create or replace function fn_unit_locale_history_immutable()
    returns trigger as
$$
begin
    raise exception 'unit_locale_history rows are append-only and cannot be updated or deleted';
end;
$$ language plpgsql;

drop trigger if exists trg_unit_locale_history_immutable on unit_locale_history;
create trigger trg_unit_locale_history_immutable
    before update or delete
    on unit_locale_history
    for each row
execute function fn_unit_locale_history_immutable();

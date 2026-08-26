create table if not exists countries
(
    id         bigserial primary key,

    code       varchar(10)                  not null unique,
    iso3_code  varchar(3)                   not null,
    phone_code varchar(3)                   not null,
    flag_url   text                         not null default '',
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

create table if not exists country_locales
(
    id          bigserial primary key,

    country_id  bigint references countries (id) on delete cascade not null,
    locale_id   bigint references locales (id) on delete restrict  not null,

    name        varchar(255)                                       not null,
    description text                                               not null default '',
    sort_order  integer                                            not null default 0,

    created_by  bigint references users (id)                       not null,
    created_at  timestamp with time zone                           not null default current_timestamp,
    updated_by  bigint references users (id)                       not null,
    updated_at  timestamp with time zone                           not null default current_timestamp,
    version     bigint                                             not null default 0,
    is_active   boolean                                            not null default true,
    is_deleted  boolean                                            not null default false,
    deleted_by  bigint references users (id),
    deleted_at  timestamp with time zone,

    constraint uq_country_locales_country_locale
        unique (country_id, locale_id)
);

insert into countries (code,
                       iso3_code,
                       phone_code,
                       sort_order,
                       created_by,
                       updated_by)
values ('BD',
        'BGD',
        '880',
        1,
        (select id from users where username = 'system'),
        (select id from users where username = 'system'));

insert into country_locales (country_id,
                             locale_id,
                             name,
                             description,
                             sort_order,
                             created_by,
                             updated_by)
values ((select id from countries where code = 'BD'),
        (select id from locales where code = 'en'),
        'Bangladesh',
        'Bangladesh is a South Asian country known for its rivers, culture, and hospitality.',
        1,
        (select id from users where username = 'system'),
        (select id from users where username = 'system'));

insert into country_locales (country_id,
                             locale_id,
                             name,
                             description,
                             sort_order,
                             created_by,
                             updated_by)
values ((select id from countries where code = 'BD'),
        (select id from locales where code = 'bn'),
        'বাংলাদেশ',
        'বাংলাদেশ দক্ষিণ এশিয়ার একটি দেশ, যা নদী, সংস্কৃতি ও আতিথেয়তার জন্য পরিচিত।',
        2,
        (select id from users where username = 'system'),
        (select id from users where username = 'system'));

insert into countries (code,
                       iso3_code,
                       phone_code,
                       sort_order,
                       created_by,
                       updated_by)
values ('US',
        'USA',
        '1',
        2,
        (select id from users where username = 'system'),
        (select id from users where username = 'system'));

insert into country_locales (country_id,
                             locale_id,
                             name,
                             description,
                             sort_order,
                             created_by,
                             updated_by)
values ((select id from countries where code = 'US'),
        (select id from locales where code = 'en'),
        'United States',
        'The United States is a North American country known for its diversity and economic influence.',
        1,
        (select id from users where username = 'system'),
        (select id from users where username = 'system')),
       ((select id from countries where code = 'US'),
        (select id from locales where code = 'bn'),
        'যুক্তরাষ্ট্র',
        'যুক্তরাষ্ট্র উত্তর আমেরিকার একটি দেশ, যা বৈচিত্র্য ও অর্থনৈতিক প্রভাবের জন্য পরিচিত।',
        2,
        (select id from users where username = 'system'),
        (select id from users where username = 'system'));


-- ============================================================
-- Audit history: append-only snapshot of every insert/update/
-- delete on countries, written by a trigger so it can't be
-- bypassed by any caller.
-- ============================================================

create table if not exists country_history
(
    id         bigserial primary key,

    -- The countries row this history entry is a snapshot of. Not a FK: a
    -- history row must survive its parent row being hard-deleted.
    country_id bigint                       not null,

    -- Which kind of change produced this snapshot.
    operation  varchar(10)                  not null check (operation in ('INSERT', 'UPDATE', 'DELETE')),

    -- Snapshot of countries.code at the time of the change.
    code       varchar(10),

    -- Snapshot of countries.iso3_code at the time of the change.
    iso3_code  varchar(3),

    -- Snapshot of countries.phone_code at the time of the change.
    phone_code varchar(3),

    -- Snapshot of countries.flag_url at the time of the change.
    flag_url   text,

    -- Snapshot of countries.sort_order at the time of the change.
    sort_order integer,

    -- Snapshot of countries.is_active at the time of the change.
    is_active  boolean,

    -- Snapshot of countries.is_deleted at the time of the change.
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
    -- blocked entirely (see trg_country_history_immutable below).
    version    bigint                       not null default 0,
    -- Kept for column-naming consistency; always null, since deletes are
    -- blocked entirely (see trg_country_history_immutable below).
    deleted_by bigint references users (id),
    deleted_at timestamp with time zone
);

create index if not exists idx_country_history_country
    on country_history (country_id);

create or replace function trg_countries_audit()
    returns trigger as
$$
begin
    if (tg_op = 'INSERT') then
        insert into country_history (country_id, operation, code, iso3_code, phone_code, flag_url, sort_order,
                                      is_active, is_deleted, created_by, updated_by)
        values (new.id, 'INSERT', new.code, new.iso3_code, new.phone_code, new.flag_url, new.sort_order,
                new.is_active, new.is_deleted, new.created_by, new.created_by);
        return new;
    elsif (tg_op = 'UPDATE') then
        insert into country_history (country_id, operation, code, iso3_code, phone_code, flag_url, sort_order,
                                      is_active, is_deleted, created_by, updated_by)
        values (new.id, 'UPDATE', new.code, new.iso3_code, new.phone_code, new.flag_url, new.sort_order,
                new.is_active, new.is_deleted, new.updated_by, new.updated_by);
        return new;
    elsif (tg_op = 'DELETE') then
        insert into country_history (country_id, operation, code, iso3_code, phone_code, flag_url, sort_order,
                                      is_active, is_deleted, created_by, updated_by)
        values (old.id, 'DELETE', old.code, old.iso3_code, old.phone_code, old.flag_url, old.sort_order,
                old.is_active, old.is_deleted, old.deleted_by, old.deleted_by);
        return old;
    end if;
    return null;
end;
$$ language plpgsql;

drop trigger if exists trg_countries_audit on countries;
create trigger trg_countries_audit
    after insert or update or delete
    on countries
    for each row
execute function trg_countries_audit();

create or replace function fn_country_history_immutable()
    returns trigger as
$$
begin
    raise exception 'country_history rows are append-only and cannot be updated or deleted';
end;
$$ language plpgsql;

drop trigger if exists trg_country_history_immutable on country_history;
create trigger trg_country_history_immutable
    before update or delete
    on country_history
    for each row
execute function fn_country_history_immutable();


-- ============================================================
-- Audit history: append-only snapshot of every insert/update/
-- delete on country_locales, written by a trigger so it can't
-- be bypassed by any caller.
-- ============================================================

create table if not exists country_locale_history
(
    id                bigserial primary key,

    -- The country_locales row this history entry is a snapshot of. Not a
    -- FK: a history row must survive its parent row being hard-deleted.
    country_locale_id bigint                       not null,

    -- Which kind of change produced this snapshot.
    operation         varchar(10)                  not null check (operation in ('INSERT', 'UPDATE', 'DELETE')),

    -- Snapshot of country_locales.country_id at the time of the change.
    country_id        bigint,

    -- Snapshot of country_locales.locale_id at the time of the change.
    locale_id         bigint,

    -- Snapshot of country_locales.name at the time of the change.
    name              varchar(255),

    -- Snapshot of country_locales.description at the time of the change.
    description       text,

    -- Snapshot of country_locales.sort_order at the time of the change.
    sort_order        integer,

    -- Snapshot of country_locales.is_active at the time of the change.
    is_active         boolean,

    -- Snapshot of country_locales.is_deleted at the time of the change.
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
    -- blocked entirely (see trg_country_locale_history_immutable below).
    version           bigint                       not null default 0,
    -- Kept for column-naming consistency; always null, since deletes are
    -- blocked entirely (see trg_country_locale_history_immutable below).
    deleted_by        bigint references users (id),
    deleted_at        timestamp with time zone
);

create index if not exists idx_country_locale_history_country_locale
    on country_locale_history (country_locale_id);

create or replace function trg_country_locales_audit()
    returns trigger as
$$
begin
    if (tg_op = 'INSERT') then
        insert into country_locale_history (country_locale_id, operation, country_id, locale_id, name, description,
                                              sort_order, is_active, is_deleted, created_by, updated_by)
        values (new.id, 'INSERT', new.country_id, new.locale_id, new.name, new.description, new.sort_order,
                new.is_active, new.is_deleted, new.created_by, new.created_by);
        return new;
    elsif (tg_op = 'UPDATE') then
        insert into country_locale_history (country_locale_id, operation, country_id, locale_id, name, description,
                                              sort_order, is_active, is_deleted, created_by, updated_by)
        values (new.id, 'UPDATE', new.country_id, new.locale_id, new.name, new.description, new.sort_order,
                new.is_active, new.is_deleted, new.updated_by, new.updated_by);
        return new;
    elsif (tg_op = 'DELETE') then
        insert into country_locale_history (country_locale_id, operation, country_id, locale_id, name, description,
                                              sort_order, is_active, is_deleted, created_by, updated_by)
        values (old.id, 'DELETE', old.country_id, old.locale_id, old.name, old.description, old.sort_order,
                old.is_active, old.is_deleted, old.deleted_by, old.deleted_by);
        return old;
    end if;
    return null;
end;
$$ language plpgsql;

drop trigger if exists trg_country_locales_audit on country_locales;
create trigger trg_country_locales_audit
    after insert or update or delete
    on country_locales
    for each row
execute function trg_country_locales_audit();

create or replace function fn_country_locale_history_immutable()
    returns trigger as
$$
begin
    raise exception 'country_locale_history rows are append-only and cannot be updated or deleted';
end;
$$ language plpgsql;

drop trigger if exists trg_country_locale_history_immutable on country_locale_history;
create trigger trg_country_locale_history_immutable
    before update or delete
    on country_locale_history
    for each row
execute function fn_country_locale_history_immutable();

create table if not exists cities
(
    id         bigserial primary key,

    country_id bigint references countries (id) on delete restrict not null,

    code       char(3)                                             not null unique,
    sort_order integer                                             not null default 0,

    created_by bigint references users (id)                        not null,
    created_at timestamp with time zone                            not null default current_timestamp,
    updated_by bigint references users (id)                        not null,
    updated_at timestamp with time zone                            not null default current_timestamp,
    version    bigint                                              not null default 0,
    is_active  boolean                                             not null default true,
    is_deleted boolean                                             not null default false,
    deleted_by bigint references users (id),
    deleted_at timestamp with time zone
);

create table if not exists city_locales
(
    id          bigserial primary key,

    city_id     bigint references cities (id) on delete cascade   not null,
    locale_id   bigint references locales (id) on delete restrict not null,

    name        varchar(255)                                      not null,
    description text                                              not null default '',
    sort_order  integer                                           not null default 0,

    created_by  bigint references users (id)                      not null,
    created_at  timestamp with time zone                          not null default current_timestamp,
    updated_by  bigint references users (id)                      not null,
    updated_at  timestamp with time zone                          not null default current_timestamp,
    version     bigint                                            not null default 0,
    is_active   boolean                                           not null default true,
    is_deleted  boolean                                           not null default false,
    deleted_by  bigint references users (id),
    deleted_at  timestamp with time zone,

    constraint uq_city_locales_city_locale
        unique (city_id, locale_id)
);

insert into cities (code, country_id, sort_order, created_by, updated_by)
values ('DHK', (select id from countries where code = 'BD'), 1, (select id from users where username = 'system'),
        (select id from users where username = 'system')),
       ('CTG', (select id from countries where code = 'BD'), 2, (select id from users where username = 'system'),
        (select id from users where username = 'system')),
       ('SYL', (select id from countries where code = 'BD'), 3, (select id from users where username = 'system'),
        (select id from users where username = 'system')),
       ('RAJ', (select id from countries where code = 'BD'), 4, (select id from users where username = 'system'),
        (select id from users where username = 'system')),
       ('KHL', (select id from countries where code = 'BD'), 5, (select id from users where username = 'system'),
        (select id from users where username = 'system')),
       ('BAR', (select id from countries where code = 'BD'), 6, (select id from users where username = 'system'),
        (select id from users where username = 'system')),
       ('RNG', (select id from countries where code = 'BD'), 7, (select id from users where username = 'system'),
        (select id from users where username = 'system')),
       ('MYM', (select id from countries where code = 'BD'), 8, (select id from users where username = 'system'),
        (select id from users where username = 'system'));

insert into city_locales (city_id,
                          locale_id,
                          name,
                          description,
                          sort_order,
                          created_by,
                          updated_by)
values ((select id from cities where code = 'DHK'),
        (select id from locales where code = 'en'),
        'Dhaka',
        'Capital city of Bangladesh',
        1, (select id from users where username = 'system'), (select id from users where username = 'system')),

       ((select id from cities where code = 'CTG'),
        (select id from locales where code = 'en'),
        'Chittagong',
        'Major seaport city of Bangladesh',
        1, (select id from users where username = 'system'), (select id from users where username = 'system')),

       ((select id from cities where code = 'SYL'),
        (select id from locales where code = 'en'),
        'Sylhet',
        'City known for tea gardens and hills',
        1, (select id from users where username = 'system'), (select id from users where username = 'system')),

       ((select id from cities where code = 'RAJ'),
        (select id from locales where code = 'en'),
        'Rajshahi',
        'City of silk and mangoes',
        1, (select id from users where username = 'system'), (select id from users where username = 'system')),

       ((select id from cities where code = 'KHL'),
        (select id from locales where code = 'en'),
        'Khulna',
        'Gateway to the Sundarbans',
        1, (select id from users where username = 'system'), (select id from users where username = 'system')),

       ((select id from cities where code = 'BAR'),
        (select id from locales where code = 'en'),
        'Barisal',
        'River-centric city of southern Bangladesh',
        1, (select id from users where username = 'system'), (select id from users where username = 'system')),

       ((select id from cities where code = 'RNG'),
        (select id from locales where code = 'en'),
        'Rangpur',
        'Administrative city of northern Bangladesh',
        1, (select id from users where username = 'system'), (select id from users where username = 'system')),

       ((select id from cities where code = 'MYM'),
        (select id from locales where code = 'en'),
        'Mymensingh',
        'City of education and culture',
        1, (select id from users where username = 'system'), (select id from users where username = 'system'));

insert into city_locales (city_id,
                          locale_id,
                          name,
                          description,
                          sort_order,
                          created_by,
                          updated_by)
values ((select id from cities where code = 'DHK'),
        (select id from locales where code = 'bn'),
        'ঢাকা',
        'বাংলাদেশের রাজধানী শহর',
        2, (select id from users where username = 'system'), (select id from users where username = 'system')),

       ((select id from cities where code = 'CTG'),
        (select id from locales where code = 'bn'),
        'চট্টগ্রাম',
        'বাংলাদেশের প্রধান সমুদ্রবন্দর শহর',
        2, (select id from users where username = 'system'), (select id from users where username = 'system')),

       ((select id from cities where code = 'SYL'),
        (select id from locales where code = 'bn'),
        'সিলেট',
        'চা বাগান ও পাহাড়ের জন্য পরিচিত শহর',
        2, (select id from users where username = 'system'), (select id from users where username = 'system')),

       ((select id from cities where code = 'RAJ'),
        (select id from locales where code = 'bn'),
        'রাজশাহী',
        'রেশম ও আমের শহর',
        2, (select id from users where username = 'system'), (select id from users where username = 'system')),

       ((select id from cities where code = 'KHL'),
        (select id from locales where code = 'bn'),
        'খুলনা',
        'সুন্দরবনের প্রবেশদ্বার',
        2, (select id from users where username = 'system'), (select id from users where username = 'system')),

       ((select id from cities where code = 'BAR'),
        (select id from locales where code = 'bn'),
        'বরিশাল',
        'দক্ষিণ বাংলাদেশের নদীকেন্দ্রিক শহর',
        2, (select id from users where username = 'system'), (select id from users where username = 'system')),

       ((select id from cities where code = 'RNG'),
        (select id from locales where code = 'bn'),
        'রংপুর',
        'উত্তর বাংলাদেশের প্রশাসনিক শহর',
        2, (select id from users where username = 'system'), (select id from users where username = 'system')),

       ((select id from cities where code = 'MYM'),
        (select id from locales where code = 'bn'),
        'ময়মনসিংহ',
        'শিক্ষা ও সংস্কৃতির শহর',
        2, (select id from users where username = 'system'), (select id from users where username = 'system'));


-- ============================================================
-- Audit history: append-only snapshot of every insert/update/
-- delete on cities, written by a trigger so it can't be
-- bypassed by any caller.
-- ============================================================

create table if not exists city_history
(
    id         bigserial primary key,

    -- The cities row this history entry is a snapshot of. Not a FK: a
    -- history row must survive its parent row being hard-deleted.
    city_id    bigint                       not null,

    -- Which kind of change produced this snapshot.
    operation  varchar(10)                  not null check (operation in ('INSERT', 'UPDATE', 'DELETE')),

    -- Snapshot of cities.country_id at the time of the change.
    country_id bigint,

    -- Snapshot of cities.code at the time of the change.
    code       char(3),

    -- Snapshot of cities.sort_order at the time of the change.
    sort_order integer,

    -- Snapshot of cities.is_active at the time of the change.
    is_active  boolean,

    -- Snapshot of cities.is_deleted at the time of the change.
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
    -- blocked entirely (see trg_city_history_immutable below).
    version    bigint                       not null default 0,
    -- Kept for column-naming consistency; always null, since deletes are
    -- blocked entirely (see trg_city_history_immutable below).
    deleted_by bigint references users (id),
    deleted_at timestamp with time zone
);

create index if not exists idx_city_history_city
    on city_history (city_id);

create or replace function trg_cities_audit()
    returns trigger as
$$
begin
    if (tg_op = 'INSERT') then
        insert into city_history (city_id, operation, country_id, code, sort_order, is_active, is_deleted,
                                   created_by, updated_by)
        values (new.id, 'INSERT', new.country_id, new.code, new.sort_order, new.is_active, new.is_deleted,
                new.created_by, new.created_by);
        return new;
    elsif (tg_op = 'UPDATE') then
        insert into city_history (city_id, operation, country_id, code, sort_order, is_active, is_deleted,
                                   created_by, updated_by)
        values (new.id, 'UPDATE', new.country_id, new.code, new.sort_order, new.is_active, new.is_deleted,
                new.updated_by, new.updated_by);
        return new;
    elsif (tg_op = 'DELETE') then
        insert into city_history (city_id, operation, country_id, code, sort_order, is_active, is_deleted,
                                   created_by, updated_by)
        values (old.id, 'DELETE', old.country_id, old.code, old.sort_order, old.is_active, old.is_deleted,
                old.deleted_by, old.deleted_by);
        return old;
    end if;
    return null;
end;
$$ language plpgsql;

drop trigger if exists trg_cities_audit on cities;
create trigger trg_cities_audit
    after insert or update or delete
    on cities
    for each row
execute function trg_cities_audit();

create or replace function fn_city_history_immutable()
    returns trigger as
$$
begin
    raise exception 'city_history rows are append-only and cannot be updated or deleted';
end;
$$ language plpgsql;

drop trigger if exists trg_city_history_immutable on city_history;
create trigger trg_city_history_immutable
    before update or delete
    on city_history
    for each row
execute function fn_city_history_immutable();


-- ============================================================
-- Audit history: append-only snapshot of every insert/update/
-- delete on city_locales, written by a trigger so it can't be
-- bypassed by any caller.
-- ============================================================

create table if not exists city_locale_history
(
    id             bigserial primary key,

    -- The city_locales row this history entry is a snapshot of. Not a
    -- FK: a history row must survive its parent row being hard-deleted.
    city_locale_id bigint                       not null,

    -- Which kind of change produced this snapshot.
    operation      varchar(10)                  not null check (operation in ('INSERT', 'UPDATE', 'DELETE')),

    -- Snapshot of city_locales.city_id at the time of the change.
    city_id        bigint,

    -- Snapshot of city_locales.locale_id at the time of the change.
    locale_id      bigint,

    -- Snapshot of city_locales.name at the time of the change.
    name           varchar(255),

    -- Snapshot of city_locales.description at the time of the change.
    description    text,

    -- Snapshot of city_locales.sort_order at the time of the change.
    sort_order     integer,

    -- Snapshot of city_locales.is_active at the time of the change.
    is_active      boolean,

    -- Snapshot of city_locales.is_deleted at the time of the change.
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
    -- blocked entirely (see trg_city_locale_history_immutable below).
    version        bigint                       not null default 0,
    -- Kept for column-naming consistency; always null, since deletes are
    -- blocked entirely (see trg_city_locale_history_immutable below).
    deleted_by     bigint references users (id),
    deleted_at     timestamp with time zone
);

create index if not exists idx_city_locale_history_city_locale
    on city_locale_history (city_locale_id);

create or replace function trg_city_locales_audit()
    returns trigger as
$$
begin
    if (tg_op = 'INSERT') then
        insert into city_locale_history (city_locale_id, operation, city_id, locale_id, name, description,
                                          sort_order, is_active, is_deleted, created_by, updated_by)
        values (new.id, 'INSERT', new.city_id, new.locale_id, new.name, new.description, new.sort_order,
                new.is_active, new.is_deleted, new.created_by, new.created_by);
        return new;
    elsif (tg_op = 'UPDATE') then
        insert into city_locale_history (city_locale_id, operation, city_id, locale_id, name, description,
                                          sort_order, is_active, is_deleted, created_by, updated_by)
        values (new.id, 'UPDATE', new.city_id, new.locale_id, new.name, new.description, new.sort_order,
                new.is_active, new.is_deleted, new.updated_by, new.updated_by);
        return new;
    elsif (tg_op = 'DELETE') then
        insert into city_locale_history (city_locale_id, operation, city_id, locale_id, name, description,
                                          sort_order, is_active, is_deleted, created_by, updated_by)
        values (old.id, 'DELETE', old.city_id, old.locale_id, old.name, old.description, old.sort_order,
                old.is_active, old.is_deleted, old.deleted_by, old.deleted_by);
        return old;
    end if;
    return null;
end;
$$ language plpgsql;

drop trigger if exists trg_city_locales_audit on city_locales;
create trigger trg_city_locales_audit
    after insert or update or delete
    on city_locales
    for each row
execute function trg_city_locales_audit();

create or replace function fn_city_locale_history_immutable()
    returns trigger as
$$
begin
    raise exception 'city_locale_history rows are append-only and cannot be updated or deleted';
end;
$$ language plpgsql;

drop trigger if exists trg_city_locale_history_immutable on city_locale_history;
create trigger trg_city_locale_history_immutable
    before update or delete
    on city_locale_history
    for each row
execute function fn_city_locale_history_immutable();

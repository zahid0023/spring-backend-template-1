create table if not exists countries
(
    id         bigserial primary key,

    code       varchar(10)                  not null unique,
    iso3_code  varchar(3)                   not null,
    phone_code varchar(3)                   not null,
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

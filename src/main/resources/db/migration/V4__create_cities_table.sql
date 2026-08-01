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

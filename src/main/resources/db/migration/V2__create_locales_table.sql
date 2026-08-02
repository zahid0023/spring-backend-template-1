create table if not exists locales
(
    id         bigserial primary key,

    code       varchar(50)                  not null unique,
    name       varchar(255)                 not null,
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

insert into locales (code,
                     name,
                     sort_order,
                     created_by,
                     updated_by)
values ('en', 'English', 1, (select id from users where username = 'system'),
        (select id from users where username = 'system')),
       ('bn', 'Bengali', 2, (select id from users where username = 'system'),
        (select id from users where username = 'system'));

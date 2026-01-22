create table if not exists roles
(
    id         bigserial
        primary key,
    name       varchar(50)                                        not null
        unique,
    created_by bigint                                             not null,
    created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_by bigint                                             not null,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    version    bigint                   default 0                 not null,
    is_active  boolean                  default true              not null,
    is_deleted boolean                  default false             not null,
    deleted_by bigint,
    deleted_at timestamp with time zone
);

create table if not exists permissions
(
    id          bigserial
        primary key,
    name        varchar(100)                                       not null
        unique,
    description varchar(255),
    created_by  bigint                                             not null,
    created_at  timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_by  bigint                                             not null,
    updated_at  timestamp with time zone default CURRENT_TIMESTAMP not null,
    version     bigint                   default 0                 not null,
    is_active   boolean                  default true              not null,
    is_deleted  boolean                  default false             not null,
    deleted_by  bigint,
    deleted_at  timestamp with time zone
);

create table if not exists users
(
    id         bigserial
        primary key,
    username   varchar(255)                                       not null
        unique,
    password   varchar(255)                                       not null,
    role_id    bigint                                             not null
        references roles
            on delete restrict,
    enabled    boolean                  default true              not null,
    locked     boolean                  default false             not null,
    expired    boolean                  default false             not null,
    created_by bigint                                             not null,
    created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_by bigint                                             not null,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    version    bigint                   default 0                 not null,
    is_active  boolean                  default true              not null,
    is_deleted boolean                  default false             not null,
    deleted_by bigint,
    deleted_at timestamp with time zone
);

create table if not exists refresh_tokens
(
    id         bigserial
        primary key,
    user_id    bigint                                             not null
        references users
            on delete cascade,
    token      varchar(500)                                       not null,
    expires_at timestamp with time zone                           not null,
    created_by bigint                                             not null,
    created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_by bigint                                             not null,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    version    bigint                   default 0                 not null,
    is_active  boolean                  default true              not null,
    is_deleted boolean                  default false             not null,
    deleted_by bigint,
    deleted_at timestamp with time zone
);

create table if not exists user_permissions
(
    id            bigserial
        primary key,
    user_id       bigint                                             not null
        references users
            on delete cascade,
    permission_id bigint                                             not null
        references permissions
            on delete cascade,
    created_by    bigint                                             not null,
    created_at    timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_by    bigint                                             not null,
    updated_at    timestamp with time zone default CURRENT_TIMESTAMP not null,
    version       bigint                   default 0                 not null,
    is_active     boolean                  default true              not null,
    is_deleted    boolean                  default false             not null,
    deleted_by    bigint,
    deleted_at    timestamp with time zone,
    unique (user_id, permission_id)
);

create table if not exists password_reset_otps
(
    id         bigserial
        primary key,
    user_id    bigint                                             not null
        references users
            on delete cascade,
    otp        varchar(255)                                       not null,
    is_used    boolean                  default false             not null,
    expires_at timestamp with time zone                           not null,
    created_by bigint                                             not null,
    created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_by bigint                                             not null,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    version    bigint                   default 0                 not null,
    is_active  boolean                  default true              not null,
    is_deleted boolean                  default false             not null,
    deleted_by bigint,
    deleted_at timestamp with time zone
);

create table if not exists reset_tokens
(
    id         bigserial
        primary key,
    user_id    bigint                                             not null
        references users
            on delete cascade,
    token      varchar(128)                                       not null,
    is_used    boolean                  default false             not null,
    expires_at timestamp with time zone                           not null,
    created_by bigint                                             not null,
    created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_by bigint                                             not null,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    version    bigint                   default 0                 not null,
    is_active  boolean                  default true              not null,
    is_deleted boolean                  default false             not null,
    deleted_by bigint,
    deleted_at timestamp with time zone
);

insert into roles(name, created_by, updated_by)
values ('USER', 1, 1)
on conflict do nothing;
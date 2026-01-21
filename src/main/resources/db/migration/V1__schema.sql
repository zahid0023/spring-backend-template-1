create table roles
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

create table permissions
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

create table users
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

create table refresh_tokens
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

create table user_permissions
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

create table reset_tokens
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
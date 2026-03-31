create table if not exists template
(
    id          bigserial primary key,
    key         varchar(100)                                       not null,
    name        varchar(150)                                       not null,
    description text,
    status      varchar(20)              default 'draft'           not null,
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

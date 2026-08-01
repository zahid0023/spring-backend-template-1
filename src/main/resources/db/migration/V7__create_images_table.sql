create table if not exists images
(
    id          bigserial primary key,

    config_id   bigint references image_hosting_configs (id) not null,

    external_id varchar(255),
    -- cloudinary public_id / s3 key
    url         text                                         not null unique,
    caption     varchar(255),
    is_default  boolean                                      not null default false,

    sort_order  int                                          not null default 0,

    created_by  bigint references users (id)                 not null,
    created_at  timestamp with time zone                     not null default current_timestamp,
    updated_by  bigint references users (id)                 not null,
    updated_at  timestamp with time zone                     not null default current_timestamp,
    version     bigint                                       not null default 0,
    is_active   boolean                                      not null default true,
    is_deleted  boolean                                      not null default false,
    deleted_by  bigint references users (id),
    deleted_at  timestamp with time zone
);

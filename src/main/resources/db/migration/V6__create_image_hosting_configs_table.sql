create table if not exists image_hosting_configs
(
    id         bigserial primary key,

    name       varchar(100)                 not null,
    -- e.g. "Cloudinary Marketing", "Cloudinary Food", "S3 Backup"

    provider   varchar(50)                  not null,
    -- e.g. CLOUDINARY, S3

    config     jsonb                        not null,

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

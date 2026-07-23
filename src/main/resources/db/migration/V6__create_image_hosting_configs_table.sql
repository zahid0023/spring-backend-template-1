CREATE TABLE IF NOT EXISTS image_hosting_configs
(
    id         bigserial primary key,

    name       varchar(100)                                       NOT NULL,
    -- e.g. "Cloudinary Marketing", "Cloudinary Food", "S3 Backup"

    provider   varchar(50)                                        not null,
    -- e.g. CLOUDINARY, S3

    config     jsonb                                              not null,

    created_by bigint references users (id)                       not null,
    created_at timestamp with time zone default current_timestamp not null,
    updated_by bigint references users (id)                       not null,
    updated_at timestamp with time zone default current_timestamp not null,
    version    bigint                   default 0                 not null,
    is_active  boolean                  default true              not null,
    is_deleted boolean                  default false             not null,
    deleted_by bigint,
    deleted_at timestamp with time zone
);

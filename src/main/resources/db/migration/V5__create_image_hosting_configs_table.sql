create table if not exists image_hosting_configs
(
    id                        bigserial primary key,

    image_hosting_provider_id bigint references image_hosting_providers (id) not null,

    -- e.g. "Cloudinary Marketing", "Cloudinary Food", "S3 Backup"
    name                      varchar(100)                                   not null,

    config                    jsonb                                          not null,

    created_by                bigint references users (id)                   not null,
    created_at                timestamp with time zone                       not null default current_timestamp,
    updated_by                bigint references users (id)                   not null,
    updated_at                timestamp with time zone                       not null default current_timestamp,
    version                   bigint                                         not null default 0,
    is_active                 boolean                                        not null default true,
    is_deleted                boolean                                        not null default false,
    deleted_by                bigint references users (id),
    deleted_at                timestamp with time zone
);

CREATE TABLE IF NOT EXISTS images
(
    id          bigserial PRIMARY KEY,

    config_id   bigint                         NOT NULL
        REFERENCES image_hosting_configs (id),

    external_id varchar(255),
    -- cloudinary public_id / s3 key
    url         text                           NOT NULL UNIQUE,
    caption     varchar(255),
    is_default  boolean                        NOT NULL DEFAULT false,

    sort_order  int                            NOT NULL DEFAULT 0,

    created_by  bigint                         not null references users (id),
    created_at  timestamptz                    not null default current_timestamp,
    updated_by  bigint                         not null references users (id),
    updated_at  timestamptz                    not null default current_timestamp,
    version     bigint                         not null default 0,
    is_active   boolean                        not null default true,
    is_deleted  boolean                        not null default false,
    deleted_by  bigint,
    deleted_at  timestamptz
);

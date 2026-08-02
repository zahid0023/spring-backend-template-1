create table if not exists image_hosting_providers
(
    id          bigserial primary key,

    -- CLOUDINARY
    -- AWS_S3
    -- CLOUDFLARE_R2
    -- AZURE_BLOB
    -- GOOGLE_CLOUD_STORAGE
    code        varchar(50)                  not null unique,

    name        varchar(100)                 not null,
    description text                         not null default '',

    sort_order  integer                      not null default 0,

    created_by  bigint references users (id) not null,
    created_at  timestamp with time zone     not null default current_timestamp,
    updated_by  bigint references users (id) not null,
    updated_at  timestamp with time zone     not null default current_timestamp,
    version     bigint                       not null default 0,
    is_active   boolean                      not null default true,
    is_deleted  boolean                      not null default false,
    deleted_by  bigint references users (id),
    deleted_at  timestamp with time zone
);

create table if not exists image_hosting_provider_config_fields
(
    id                        bigserial primary key,

    image_hosting_provider_id bigint references image_hosting_providers (id) not null,

    key                       varchar(100)                                   not null,
    label                     varchar(100)                                   not null,
    -- TEXT
    -- PASSWORD
    -- NUMBER
    -- BOOLEAN
    -- URL
    field_type                varchar(30)                                    not null,

    placeholder               varchar(255)                                   not null default '',
    default_value             varchar(500)                                   not null default '',
    is_required               boolean                                        not null default true,

    sort_order                integer                                        not null default 0,

    created_by                bigint references users (id)                   not null,
    created_at                timestamp with time zone                       not null default current_timestamp,
    updated_by                bigint references users (id)                   not null,
    updated_at                timestamp with time zone                       not null default current_timestamp,
    version                   bigint                                         not null default 0,
    is_active                 boolean                                        not null default true,
    is_deleted                boolean                                        not null default false,
    deleted_by                bigint references users (id),
    deleted_at                timestamp with time zone,

    constraint uq_provider_key
        unique (image_hosting_provider_id, key)
);

insert into image_hosting_providers (code, name, description, sort_order, created_by, updated_by)
values ('AWS_S3', 'Amazon S3', '', 1, (select id from users where username = 'system'),
        (select id from users where username = 'system')),
       ('CLOUDINARY', 'Cloudinary', '', 2, (select id from users where username = 'system'),
        (select id from users where username = 'system')),
       ('CLOUDFLARE_R2', 'Cloudflare R2', '', 3, (select id from users where username = 'system'),
        (select id from users where username = 'system'));

insert into image_hosting_provider_config_fields (image_hosting_provider_id, key, label, field_type, is_required,
                                                  sort_order, created_by, updated_by)
values
-- AWS_S3
((select id from image_hosting_providers where code = 'AWS_S3'), 'bucket', 'Bucket Name', 'TEXT', true, 1,
 (select id from users where username = 'system'), (select id from users where username = 'system')),
((select id from image_hosting_providers where code = 'AWS_S3'), 'region', 'Region', 'TEXT', true, 2,
 (select id from users where username = 'system'), (select id from users where username = 'system')),
((select id from image_hosting_providers where code = 'AWS_S3'), 'accessKey', 'Access Key', 'PASSWORD', true, 3,
 (select id from users where username = 'system'), (select id from users where username = 'system')),
((select id from image_hosting_providers where code = 'AWS_S3'), 'secretKey', 'Secret Key', 'PASSWORD', true, 4,
 (select id from users where username = 'system'), (select id from users where username = 'system')),

-- CLOUDINARY
((select id from image_hosting_providers where code = 'CLOUDINARY'), 'cloudName', 'Cloud Name', 'TEXT', true, 1,
 (select id from users where username = 'system'), (select id from users where username = 'system')),
((select id from image_hosting_providers where code = 'CLOUDINARY'), 'apiKey', 'API Key', 'PASSWORD', true, 2,
 (select id from users where username = 'system'), (select id from users where username = 'system')),
((select id from image_hosting_providers where code = 'CLOUDINARY'), 'apiSecret', 'API Secret', 'PASSWORD', true, 3,
 (select id from users where username = 'system'), (select id from users where username = 'system')),
((select id from image_hosting_providers where code = 'CLOUDINARY'), 'folder', 'Folder', 'TEXT', false, 4,
 (select id from users where username = 'system'), (select id from users where username = 'system')),

-- CLOUDFLARE_R2
((select id from image_hosting_providers where code = 'CLOUDFLARE_R2'), 'accountId', 'Account ID', 'TEXT', true, 1,
 (select id from users where username = 'system'), (select id from users where username = 'system')),
((select id from image_hosting_providers where code = 'CLOUDFLARE_R2'), 'bucket', 'Bucket', 'TEXT', true, 2,
 (select id from users where username = 'system'), (select id from users where username = 'system')),
((select id from image_hosting_providers where code = 'CLOUDFLARE_R2'), 'accessKey', 'Access Key', 'PASSWORD', true,
 3, (select id from users where username = 'system'), (select id from users where username = 'system')),
((select id from image_hosting_providers where code = 'CLOUDFLARE_R2'), 'secretKey', 'Secret Key', 'PASSWORD', true,
 4, (select id from users where username = 'system'), (select id from users where username = 'system'));
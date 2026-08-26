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


-- ============================================================
-- Audit history: append-only snapshot of every insert/update/
-- delete on image_hosting_providers, written by a trigger so
-- it can't be bypassed by any caller.
-- ============================================================

create table if not exists image_hosting_provider_history
(
    id                        bigserial primary key,

    -- The image_hosting_providers row this history entry is a snapshot of.
    -- Not a FK: a history row must survive its parent row being hard-deleted.
    image_hosting_provider_id bigint                       not null,

    -- Which kind of change produced this snapshot.
    operation                 varchar(10)                  not null check (operation in ('INSERT', 'UPDATE', 'DELETE')),

    -- Snapshot of image_hosting_providers.code at the time of the change.
    code                      varchar(50),

    -- Snapshot of image_hosting_providers.name at the time of the change.
    name                      varchar(100),

    -- Snapshot of image_hosting_providers.description at the time of the change.
    description               text,

    -- Snapshot of image_hosting_providers.sort_order at the time of the change.
    sort_order                integer,

    -- Snapshot of image_hosting_providers.is_active at the time of the change.
    is_active                 boolean,

    -- Snapshot of image_hosting_providers.is_deleted at the time of the change.
    is_deleted                boolean,

    -- Who/when this history row was written — same person/time as the
    -- created_by/updated_by/deleted_by that triggered it, depending on operation.
    created_by                bigint references users (id) not null,
    created_at                timestamp with time zone     not null default current_timestamp,
    -- Kept for column-naming consistency with every other table; always equal
    -- to created_by/created_at since a history row is never actually updated.
    updated_by                bigint references users (id) not null,
    updated_at                timestamp with time zone     not null default current_timestamp,
    -- Kept for column-naming consistency; never increments, since updates are
    -- blocked entirely (see trg_image_hosting_provider_history_immutable below).
    version                   bigint                       not null default 0,
    -- Kept for column-naming consistency; always null, since deletes are
    -- blocked entirely (see trg_image_hosting_provider_history_immutable below).
    deleted_by                bigint references users (id),
    deleted_at                timestamp with time zone
);

create index if not exists idx_image_hosting_provider_history_provider
    on image_hosting_provider_history (image_hosting_provider_id);

create or replace function trg_image_hosting_providers_audit()
    returns trigger as
$$
begin
    if (tg_op = 'INSERT') then
        insert into image_hosting_provider_history (image_hosting_provider_id, operation, code, name, description,
                                                      sort_order, is_active, is_deleted, created_by, updated_by)
        values (new.id, 'INSERT', new.code, new.name, new.description, new.sort_order, new.is_active,
                new.is_deleted, new.created_by, new.created_by);
        return new;
    elsif (tg_op = 'UPDATE') then
        insert into image_hosting_provider_history (image_hosting_provider_id, operation, code, name, description,
                                                      sort_order, is_active, is_deleted, created_by, updated_by)
        values (new.id, 'UPDATE', new.code, new.name, new.description, new.sort_order, new.is_active,
                new.is_deleted, new.updated_by, new.updated_by);
        return new;
    elsif (tg_op = 'DELETE') then
        insert into image_hosting_provider_history (image_hosting_provider_id, operation, code, name, description,
                                                      sort_order, is_active, is_deleted, created_by, updated_by)
        values (old.id, 'DELETE', old.code, old.name, old.description, old.sort_order, old.is_active,
                old.is_deleted, old.deleted_by, old.deleted_by);
        return old;
    end if;
    return null;
end;
$$ language plpgsql;

drop trigger if exists trg_image_hosting_providers_audit on image_hosting_providers;
create trigger trg_image_hosting_providers_audit
    after insert or update or delete
    on image_hosting_providers
    for each row
execute function trg_image_hosting_providers_audit();

create or replace function fn_image_hosting_provider_history_immutable()
    returns trigger as
$$
begin
    raise exception 'image_hosting_provider_history rows are append-only and cannot be updated or deleted';
end;
$$ language plpgsql;

drop trigger if exists trg_image_hosting_provider_history_immutable on image_hosting_provider_history;
create trigger trg_image_hosting_provider_history_immutable
    before update or delete
    on image_hosting_provider_history
    for each row
execute function fn_image_hosting_provider_history_immutable();


-- ============================================================
-- Audit history: append-only snapshot of every insert/update/
-- delete on image_hosting_provider_config_fields, written by
-- a trigger so it can't be bypassed by any caller.
-- ============================================================

create table if not exists image_hosting_provider_config_field_history
(
    id                                      bigserial primary key,

    -- The image_hosting_provider_config_fields row this history entry is a snapshot of.
    -- Not a FK: a history row must survive its parent row being hard-deleted.
    image_hosting_provider_config_field_id bigint                       not null,

    -- Which kind of change produced this snapshot.
    operation                               varchar(10)                  not null check (operation in ('INSERT', 'UPDATE', 'DELETE')),

    -- Snapshot of image_hosting_provider_config_fields.image_hosting_provider_id at the time of the change.
    image_hosting_provider_id               bigint,

    -- Snapshot of image_hosting_provider_config_fields.key at the time of the change.
    key                                      varchar(100),

    -- Snapshot of image_hosting_provider_config_fields.label at the time of the change.
    label                                    varchar(100),

    -- Snapshot of image_hosting_provider_config_fields.field_type at the time of the change.
    field_type                               varchar(30),

    -- Snapshot of image_hosting_provider_config_fields.placeholder at the time of the change.
    placeholder                              varchar(255),

    -- Snapshot of image_hosting_provider_config_fields.default_value at the time of the change.
    default_value                            varchar(500),

    -- Snapshot of image_hosting_provider_config_fields.is_required at the time of the change.
    is_required                              boolean,

    -- Snapshot of image_hosting_provider_config_fields.sort_order at the time of the change.
    sort_order                               integer,

    -- Snapshot of image_hosting_provider_config_fields.is_active at the time of the change.
    is_active                                boolean,

    -- Snapshot of image_hosting_provider_config_fields.is_deleted at the time of the change.
    is_deleted                               boolean,

    -- Who/when this history row was written — same person/time as the
    -- created_by/updated_by/deleted_by that triggered it, depending on operation.
    created_by                               bigint references users (id) not null,
    created_at                               timestamp with time zone     not null default current_timestamp,
    -- Kept for column-naming consistency with every other table; always equal
    -- to created_by/created_at since a history row is never actually updated.
    updated_by                               bigint references users (id) not null,
    updated_at                               timestamp with time zone     not null default current_timestamp,
    -- Kept for column-naming consistency; never increments, since updates are
    -- blocked entirely (see trg_image_hosting_provider_config_field_history_immutable below).
    version                                  bigint                       not null default 0,
    -- Kept for column-naming consistency; always null, since deletes are
    -- blocked entirely (see trg_image_hosting_provider_config_field_history_immutable below).
    deleted_by                               bigint references users (id),
    deleted_at                               timestamp with time zone
);

create index if not exists idx_image_hosting_provider_config_field_history_field
    on image_hosting_provider_config_field_history (image_hosting_provider_config_field_id);

create or replace function trg_image_hosting_provider_config_fields_audit()
    returns trigger as
$$
begin
    if (tg_op = 'INSERT') then
        insert into image_hosting_provider_config_field_history
            (image_hosting_provider_config_field_id, operation, image_hosting_provider_id, key, label, field_type,
             placeholder, default_value, is_required, sort_order, is_active, is_deleted, created_by, updated_by)
        values (new.id, 'INSERT', new.image_hosting_provider_id, new.key, new.label, new.field_type,
                new.placeholder, new.default_value, new.is_required, new.sort_order, new.is_active,
                new.is_deleted, new.created_by, new.created_by);
        return new;
    elsif (tg_op = 'UPDATE') then
        insert into image_hosting_provider_config_field_history
            (image_hosting_provider_config_field_id, operation, image_hosting_provider_id, key, label, field_type,
             placeholder, default_value, is_required, sort_order, is_active, is_deleted, created_by, updated_by)
        values (new.id, 'UPDATE', new.image_hosting_provider_id, new.key, new.label, new.field_type,
                new.placeholder, new.default_value, new.is_required, new.sort_order, new.is_active,
                new.is_deleted, new.updated_by, new.updated_by);
        return new;
    elsif (tg_op = 'DELETE') then
        insert into image_hosting_provider_config_field_history
            (image_hosting_provider_config_field_id, operation, image_hosting_provider_id, key, label, field_type,
             placeholder, default_value, is_required, sort_order, is_active, is_deleted, created_by, updated_by)
        values (old.id, 'DELETE', old.image_hosting_provider_id, old.key, old.label, old.field_type,
                old.placeholder, old.default_value, old.is_required, old.sort_order, old.is_active,
                old.is_deleted, old.deleted_by, old.deleted_by);
        return old;
    end if;
    return null;
end;
$$ language plpgsql;

drop trigger if exists trg_image_hosting_provider_config_fields_audit on image_hosting_provider_config_fields;
create trigger trg_image_hosting_provider_config_fields_audit
    after insert or update or delete
    on image_hosting_provider_config_fields
    for each row
execute function trg_image_hosting_provider_config_fields_audit();

create or replace function fn_image_hosting_provider_config_field_history_immutable()
    returns trigger as
$$
begin
    raise exception 'image_hosting_provider_config_field_history rows are append-only and cannot be updated or deleted';
end;
$$ language plpgsql;

drop trigger if exists trg_image_hosting_provider_config_field_history_immutable on image_hosting_provider_config_field_history;
create trigger trg_image_hosting_provider_config_field_history_immutable
    before update or delete
    on image_hosting_provider_config_field_history
    for each row
execute function fn_image_hosting_provider_config_field_history_immutable();
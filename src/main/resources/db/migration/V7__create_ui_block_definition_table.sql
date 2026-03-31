create table if not exists ui_block_definition
(
    id                    bigserial primary key,
    ui_block_key          varchar(100)                                       not null,
    name                  varchar(150)                                       not null,
    description           text                                               not null,
    ui_block_version      varchar(20)              default '1.0.0'           not null,
    ui_block_category_id  bigint                                             not null references ui_block_categories (id),
    page_type_id          bigint                                             not null references page_types (id),
    editable_schema       jsonb                                              not null,
    default_content       jsonb                                              not null,
    allowed_pages         jsonb,
    status                varchar(20)              default 'draft'           not null,
    created_by            bigint                                             not null,
    created_at            timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_by            bigint                                             not null,
    updated_at            timestamp with time zone default CURRENT_TIMESTAMP not null,
    version               bigint                   default 0                 not null,
    is_active             boolean                  default true              not null,
    is_deleted            boolean                  default false             not null,
    deleted_by            bigint,
    deleted_at            timestamp with time zone
);

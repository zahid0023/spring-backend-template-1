create table if not exists template_page_slot
(
    id                    bigserial primary key,
    template_page_id      bigint                                             not null references template_page (id),
    ui_block_category_id  bigint                                             not null references ui_block_categories (id),
    slot_name             varchar(150)                                       not null,
    is_required           boolean                  default false             not null,
    slot_order            integer                                            not null,
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

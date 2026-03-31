create table if not exists template_page
(
    id           bigserial primary key,
    template_id  bigint                                             not null references template (id),
    page_type_id bigint                                             not null references page_types (id),
    page_name    varchar(150)                                       not null,
    page_slug    varchar(150)                                       not null,
    page_order   integer                                            not null,
    created_by   bigint                                             not null,
    created_at   timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_by   bigint                                             not null,
    updated_at   timestamp with time zone default CURRENT_TIMESTAMP not null,
    version      bigint                   default 0                 not null,
    is_active    boolean                  default true              not null,
    is_deleted   boolean                  default false             not null,
    deleted_by   bigint,
    deleted_at   timestamp with time zone
);

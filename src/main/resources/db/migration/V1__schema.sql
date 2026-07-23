-- ============================================================
-- Create tables (no FK constraints yet)
-- ============================================================

create table if not exists roles
(
    id         bigserial primary key,

    name       varchar(50)                                        not null unique,

    created_by bigint                                             not null,
    created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_by bigint                                             not null,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    version    bigint                   default 0                 not null,
    is_active  boolean                  default true              not null,
    is_deleted boolean                  default false             not null,
    deleted_by bigint,
    deleted_at timestamp with time zone
);

create table if not exists users
(
    id         bigserial primary key,

    username   varchar(255)                                       not null unique,
    password   varchar(255)                                       not null,
    role_id    bigint                                             not null,
    enabled    boolean                  default true              not null,
    locked     boolean                  default false             not null,
    expired    boolean                  default false             not null,

    created_by bigint                                             not null,
    created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_by bigint                                             not null,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    version    bigint                   default 0                 not null,
    is_active  boolean                  default true              not null,
    is_deleted boolean                  default false             not null,
    deleted_by bigint,
    deleted_at timestamp with time zone
);

create table if not exists permissions
(
    id          bigserial primary key,

    name        varchar(100)                                       not null unique,
    description varchar(255),

    created_by  bigint                                             not null,
    created_at  timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_by  bigint                                             not null,
    updated_at  timestamp with time zone default CURRENT_TIMESTAMP not null,
    version     bigint                   default 0                 not null,
    is_active   boolean                  default true              not null,
    is_deleted  boolean                  default false             not null,
    deleted_by  bigint,
    deleted_at  timestamp with time zone
);

create table if not exists user_permissions
(
    id            bigserial primary key,

    user_id       bigint                                             not null,
    permission_id bigint                                             not null,

    created_by    bigint                                             not null,
    created_at    timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_by    bigint                                             not null,
    updated_at    timestamp with time zone default CURRENT_TIMESTAMP not null,
    version       bigint                   default 0                 not null,
    is_active     boolean                  default true              not null,
    is_deleted    boolean                  default false             not null,
    deleted_by    bigint,
    deleted_at    timestamp with time zone,
    unique (user_id, permission_id)
);

-- ============================================================
-- Seed data — inserted before FK constraints are added,
-- so there is no circular dependency to worry about.
--
-- IDs are set explicitly so we never rely on sequence order.
--
--   roles  id=1 → SYSTEM role
--   users  id=1 → system user  (role_id=1, created_by=1 self-ref)
--   roles  id=2 → USER role    (created_by=1, system user)
-- ============================================================

insert into roles (id, name, created_by, updated_by)
values (1, 'SYSTEM', 1, 1);

insert into users (id, username, password, role_id, created_by, updated_by, enabled, locked, expired)
values (1, 'system', 'N/A', 1, 1, 1, true, false, false);

insert into roles (id, name, created_by, updated_by)
values (2, 'USER', 1, 1);

insert into roles (id, name, created_by, updated_by)
values (3, 'ADMIN', 1, 1);

insert into permissions (name, description, created_by, updated_by)
values ('CREATE_SCOPE', 'Create a new scope', 1, 1)
on conflict (name) do nothing;

-- Advance sequences so next auto-generated IDs start above the seeded values
select setval(pg_get_serial_sequence('roles', 'id'), (select max(id) from roles));
select setval(pg_get_serial_sequence('users', 'id'), (select max(id) from users));

-- ============================================================
-- FK constraints — added after seed data so PostgreSQL
-- validates the already-inserted rows (all pass).
-- ============================================================

-- Structural FKs
alter table users
    add constraint fk_users_role
        foreign key (role_id) references roles (id);

alter table user_permissions
    add constraint fk_user_permissions_user
        foreign key (user_id) references users (id);

alter table user_permissions
    add constraint fk_user_permissions_permission
        foreign key (permission_id) references permissions (id);

-- Audit FKs — roles
alter table roles
    add constraint fk_roles_created_by
        foreign key (created_by) references users (id);

alter table roles
    add constraint fk_roles_updated_by
        foreign key (updated_by) references users (id);

alter table roles
    add constraint fk_roles_deleted_by
        foreign key (deleted_by) references users (id);

-- Audit FKs — users
alter table users
    add constraint fk_users_created_by
        foreign key (created_by) references users (id);

alter table users
    add constraint fk_users_updated_by
        foreign key (updated_by) references users (id);

alter table users
    add constraint fk_users_deleted_by
        foreign key (deleted_by) references users (id);

-- Audit FKs — permissions
alter table permissions
    add constraint fk_permissions_created_by
        foreign key (created_by) references users (id);

alter table permissions
    add constraint fk_permissions_updated_by
        foreign key (updated_by) references users (id);

alter table permissions
    add constraint fk_permissions_deleted_by
        foreign key (deleted_by) references users (id);

-- Audit FKs — user_permissions
alter table user_permissions
    add constraint fk_user_permissions_created_by
        foreign key (created_by) references users (id);

alter table user_permissions
    add constraint fk_user_permissions_updated_by
        foreign key (updated_by) references users (id);

alter table user_permissions
    add constraint fk_user_permissions_deleted_by
        foreign key (deleted_by) references users (id);
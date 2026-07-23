CREATE TABLE IF NOT EXISTS locales
(
    id         bigserial PRIMARY KEY,
    code       varchar(50)                  NOT NULL UNIQUE,
    name       varchar(255)                 NOT NULL,
    sort_order integer                      NOT NULL,

    created_by bigint references users (id) NOT NULL,
    created_at timestamp with time zone     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by bigint references users (id) NOT NULL,
    updated_at timestamp with time zone     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version    bigint                                DEFAULT 0 NOT NULL,
    is_active  boolean                      NOT NULL DEFAULT true,
    is_deleted boolean                      NOT NULL DEFAULT false,
    deleted_by bigint,
    deleted_at timestamp with time zone
);

INSERT INTO locales (code,
                     name,
                     sort_order,
                     created_by,
                     updated_by)
VALUES ('en', 'English', 1, (SELECT id FROM users WHERE username = 'system'),
        (SELECT id FROM users WHERE username = 'system'));

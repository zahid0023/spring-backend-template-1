CREATE TABLE IF NOT EXISTS countries
(
    id         bigserial PRIMARY KEY,

    code       varchar(10)                  NOT NULL UNIQUE,
    iso3_code  varchar(10),
    phone_code varchar(10),
    sort_order int                          NOT NULL DEFAULT 0,

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

CREATE TABLE IF NOT EXISTS country_locales
(
    id          bigserial PRIMARY KEY,

    country_id  bigint                       NOT NULL REFERENCES countries (id) ON DELETE CASCADE,
    locale_id   bigint                       NOT NULL REFERENCES locales (id) ON DELETE RESTRICT,

    name        varchar(255)                 NOT NULL,
    description text,
    sort_order  int                          NOT NULL DEFAULT 0,

    created_by  bigint references users (id) NOT NULL,
    created_at  timestamp with time zone     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  bigint references users (id) NOT NULL,
    updated_at  timestamp with time zone     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version     bigint                                DEFAULT 0 NOT NULL,
    is_active   boolean                      NOT NULL DEFAULT true,
    is_deleted  boolean                      NOT NULL DEFAULT false,
    deleted_by  bigint,
    deleted_at  timestamp with time zone,
    UNIQUE (country_id, locale_id)
);

INSERT INTO countries (code,
                       iso3_code,
                       phone_code,
                       sort_order,
                       created_by,
                       updated_by)
VALUES ('BD',
        'BGD',
        '+880',
        1,
        (SELECT id FROM users WHERE username = 'system'),
        (SELECT id FROM users WHERE username = 'system'));

INSERT INTO country_locales (country_id,
                             locale_id,
                             name,
                             description,
                             sort_order,
                             created_by,
                             updated_by)
VALUES ((SELECT id FROM countries WHERE code = 'BD'),
        (SELECT id FROM locales WHERE code = 'en'),
        'Bangladesh',
        'Bangladesh is a South Asian country known for its rivers, culture, and hospitality.',
        1,
        (SELECT id FROM users WHERE username = 'system'),
        (SELECT id FROM users WHERE username = 'system'));

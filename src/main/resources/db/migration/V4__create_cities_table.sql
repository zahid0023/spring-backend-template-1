CREATE TABLE IF NOT EXISTS cities
(
    id         bigserial PRIMARY KEY,
    country_id bigint                       NOT NULL REFERENCES countries (id) ON DELETE RESTRICT,
    code       varchar(50),
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

CREATE TABLE IF NOT EXISTS city_locales
(
    id          bigserial PRIMARY KEY,
    city_id     bigint                       NOT NULL REFERENCES cities (id) ON DELETE CASCADE,
    locale_id   bigint                       NOT NULL REFERENCES locales (id) ON DELETE RESTRICT,

    name        varchar(255)                 NOT NULL,
    description text                         NOT NULL DEFAULT '',
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
    UNIQUE (city_id, locale_id)
);

INSERT INTO cities (code, country_id, sort_order, created_by, updated_by)
VALUES ('DHK', (SELECT id FROM countries WHERE code = 'BD'), 1, (SELECT id FROM users WHERE username = 'system'),
        (SELECT id FROM users WHERE username = 'system')),
       ('CTG', (SELECT id FROM countries WHERE code = 'BD'), 2, (SELECT id FROM users WHERE username = 'system'),
        (SELECT id FROM users WHERE username = 'system')),
       ('SYL', (SELECT id FROM countries WHERE code = 'BD'), 3, (SELECT id FROM users WHERE username = 'system'),
        (SELECT id FROM users WHERE username = 'system')),
       ('RAJ', (SELECT id FROM countries WHERE code = 'BD'), 4, (SELECT id FROM users WHERE username = 'system'),
        (SELECT id FROM users WHERE username = 'system')),
       ('KHL', (SELECT id FROM countries WHERE code = 'BD'), 5, (SELECT id FROM users WHERE username = 'system'),
        (SELECT id FROM users WHERE username = 'system')),
       ('BAR', (SELECT id FROM countries WHERE code = 'BD'), 6, (SELECT id FROM users WHERE username = 'system'),
        (SELECT id FROM users WHERE username = 'system')),
       ('RNG', (SELECT id FROM countries WHERE code = 'BD'), 7, (SELECT id FROM users WHERE username = 'system'),
        (SELECT id FROM users WHERE username = 'system')),
       ('MYM', (SELECT id FROM countries WHERE code = 'BD'), 8, (SELECT id FROM users WHERE username = 'system'),
        (SELECT id FROM users WHERE username = 'system'));

INSERT INTO city_locales (city_id,
                          locale_id,
                          name,
                          description,
                          sort_order,
                          created_by,
                          updated_by)
VALUES ((SELECT id FROM cities WHERE code = 'DHK'),
        (SELECT id FROM locales WHERE code = 'en'),
        'Dhaka',
        'Capital city of Bangladesh',
        1, (SELECT id FROM users WHERE username = 'system'), (SELECT id FROM users WHERE username = 'system')),

       ((SELECT id FROM cities WHERE code = 'CTG'),
        (SELECT id FROM locales WHERE code = 'en'),
        'Chittagong',
        'Major seaport city of Bangladesh',
        1, (SELECT id FROM users WHERE username = 'system'), (SELECT id FROM users WHERE username = 'system')),

       ((SELECT id FROM cities WHERE code = 'SYL'),
        (SELECT id FROM locales WHERE code = 'en'),
        'Sylhet',
        'City known for tea gardens and hills',
        1, (SELECT id FROM users WHERE username = 'system'), (SELECT id FROM users WHERE username = 'system')),

       ((SELECT id FROM cities WHERE code = 'RAJ'),
        (SELECT id FROM locales WHERE code = 'en'),
        'Rajshahi',
        'City of silk and mangoes',
        1, (SELECT id FROM users WHERE username = 'system'), (SELECT id FROM users WHERE username = 'system')),

       ((SELECT id FROM cities WHERE code = 'KHL'),
        (SELECT id FROM locales WHERE code = 'en'),
        'Khulna',
        'Gateway to the Sundarbans',
        1, (SELECT id FROM users WHERE username = 'system'), (SELECT id FROM users WHERE username = 'system')),

       ((SELECT id FROM cities WHERE code = 'BAR'),
        (SELECT id FROM locales WHERE code = 'en'),
        'Barisal',
        'River-centric city of southern Bangladesh',
        1, (SELECT id FROM users WHERE username = 'system'), (SELECT id FROM users WHERE username = 'system')),

       ((SELECT id FROM cities WHERE code = 'RNG'),
        (SELECT id FROM locales WHERE code = 'en'),
        'Rangpur',
        'Administrative city of northern Bangladesh',
        1, (SELECT id FROM users WHERE username = 'system'), (SELECT id FROM users WHERE username = 'system')),

       ((SELECT id FROM cities WHERE code = 'MYM'),
        (SELECT id FROM locales WHERE code = 'en'),
        'Mymensingh',
        'City of education and culture',
        1, (SELECT id FROM users WHERE username = 'system'), (SELECT id FROM users WHERE username = 'system'));

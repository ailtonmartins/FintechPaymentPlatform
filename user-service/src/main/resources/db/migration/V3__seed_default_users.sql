INSERT INTO users (id, name, email, password, enabled, created_at, updated_at)
VALUES
    (
        '11111111-1111-1111-1111-111111111111',
        'Ailton Martins',
        'ailton@email.com',
        '$2a$10$JGBmOJxFO3feFiYyu/lE4e9K.5DOUnUHUuFevxlW1HokePNnyfzz6',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        'Sara Amorim',
        'sara@email.com',
        '$2a$10$4gELt9o/GBOwwPwF2S8R0.HEZLMpnmicvAXYAXyMhuW.O6mrKenam',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        '33333333-3333-3333-3333-333333333333',
        'Administrador',
        'admin@email.com',
        '$2a$10$JGBmOJxFO3feFiYyu/lE4e9K.5DOUnUHUuFevxlW1HokePNnyfzz6',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        '44444444-4444-4444-4444-444444444444',
        'Usuario Padrao',
        'user@email.com',
        '$2a$10$JGBmOJxFO3feFiYyu/lE4e9K.5DOUnUHUuFevxlW1HokePNnyfzz6',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        '55555555-5555-5555-5555-555555555555',
        'Bruno Oliveira',
        'bruno@email.com',
        '$2a$10$JGBmOJxFO3feFiYyu/lE4e9K.5DOUnUHUuFevxlW1HokePNnyfzz6',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        '66666666-6666-6666-6666-666666666666',
        'Carla Souza',
        'carla@email.com',
        '$2a$10$JGBmOJxFO3feFiYyu/lE4e9K.5DOUnUHUuFevxlW1HokePNnyfzz6',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        '77777777-7777-7777-7777-777777777777',
        'Diego Santos',
        'diego@email.com',
        '$2a$10$JGBmOJxFO3feFiYyu/lE4e9K.5DOUnUHUuFevxlW1HokePNnyfzz6',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        '88888888-8888-8888-8888-888888888888',
        'Elaine Costa',
        'elaine@email.com',
        '$2a$10$JGBmOJxFO3feFiYyu/lE4e9K.5DOUnUHUuFevxlW1HokePNnyfzz6',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        '99999999-9999-9999-9999-999999999999',
        'Felipe Lima',
        'felipe@email.com',
        '$2a$10$JGBmOJxFO3feFiYyu/lE4e9K.5DOUnUHUuFevxlW1HokePNnyfzz6',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'Gabriela Rocha',
        'gabriela@email.com',
        '$2a$10$JGBmOJxFO3feFiYyu/lE4e9K.5DOUnUHUuFevxlW1HokePNnyfzz6',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
        'Henrique Alves',
        'henrique@email.com',
        '$2a$10$JGBmOJxFO3feFiYyu/lE4e9K.5DOUnUHUuFevxlW1HokePNnyfzz6',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'cccccccc-cccc-cccc-cccc-cccccccccccc',
        'Isabela Pereira',
        'isabela@email.com',
        '$2a$10$JGBmOJxFO3feFiYyu/lE4e9K.5DOUnUHUuFevxlW1HokePNnyfzz6',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'dddddddd-dddd-dddd-dddd-dddddddddddd',
        'Joao Ferreira',
        'joao@email.com',
        '$2a$10$JGBmOJxFO3feFiYyu/lE4e9K.5DOUnUHUuFevxlW1HokePNnyfzz6',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
        'Mariana Gomes',
        'mariana@email.com',
        '$2a$10$JGBmOJxFO3feFiYyu/lE4e9K.5DOUnUHUuFevxlW1HokePNnyfzz6',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'ffffffff-ffff-ffff-ffff-ffffffffffff',
        'Rafael Mendes',
        'rafael@email.com',
        '$2a$10$JGBmOJxFO3feFiYyu/lE4e9K.5DOUnUHUuFevxlW1HokePNnyfzz6',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

INSERT INTO user_roles (user_id, role)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'USER'),
    ('22222222-2222-2222-2222-222222222222', 'USER'),
    ('33333333-3333-3333-3333-333333333333', 'USER'),
    ('33333333-3333-3333-3333-333333333333', 'ADMIN'),
    ('44444444-4444-4444-4444-444444444444', 'USER'),
    ('55555555-5555-5555-5555-555555555555', 'USER'),
    ('66666666-6666-6666-6666-666666666666', 'USER'),
    ('77777777-7777-7777-7777-777777777777', 'USER'),
    ('88888888-8888-8888-8888-888888888888', 'USER'),
    ('99999999-9999-9999-9999-999999999999', 'USER'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'USER'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'USER'),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'USER'),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'USER'),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'USER'),
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', 'USER');

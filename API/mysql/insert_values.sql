-- AÑADE LOS IDIOMAS EN LA BASE DE DATOS
INSERT INTO languages (id, lang_code, language) VALUES
(1,'esp', 'Español'),
(2,'eng', 'English'),
(3,'fra', 'Français'),
(4,'deu', 'Deutsch'),
(5,'ita', 'Italiano');

-- AÑADE LOS PERMISOS EXISTENTES EN LA BASE DE DATOS
INSERT INTO permission (id, name) VALUES
(1,'CREATE'),
(2,'DELETE'),
(3,'UPDATE'),
(4,'READ');

-- AÑADE LOS ROLES EXISTENTES EN LA BASE DE DATOS
INSERT INTO roles (id, role_name) VALUES
(1, 'GUESS'),
(2, 'USER'),
(3, 'MODERATOR'),
(4, 'ADMIN');

-- CREA RELACIONES ENTRE ROLES Y PERMISOS
INSERT INTO roles_permissions (role_id, permission_id) VALUES
(1,4), -- guess
(2,4), -- user
(3,1), -- moderator
(3,2),
(3,3),
(4,1), -- admin
(4,2),
(4,3),
(4,4);

-- CREA USUARIOS EN LA BASE DE DATOS
INSERT INTO users (id, credential_non_expired, account_non_expired, account_non_locked, username, is_enabled, password) VALUES
(1, TRUE, TRUE, TRUE, 'gagochorenmartin@gmail.com', TRUE, '$2a$10$stG8zdkNxF6Zwnz9tnN2Meg8b4QZWW537H1zsyK3JcAc1nupsiXia'), -- admin 12345
(2, TRUE, TRUE, TRUE, 'chgnitram@gmail.com', TRUE, '$2a$10$stG8zdkNxF6Zwnz9tnN2Meg8b4QZWW537H1zsyK3JcAc1nupsiXia'), -- moderator 12345
(3, TRUE, TRUE, TRUE, 'test@test.com', TRUE, '$2a$10$stG8zdkNxF6Zwnz9tnN2Meg8b4QZWW537H1zsyK3JcAc1nupsiXia'); -- user 12345

INSERT INTO users (id, credential_non_expired, account_non_expired, account_non_locked, username, is_enabled, password) VALUES
(1, TRUE, TRUE, TRUE, 'martin', TRUE, '$2a$10$stG8zdkNxF6Zwnz9tnN2Meg8b4QZWW537H1zsyK3JcAc1nupsiXia'); -- admin 12345

-- ASIGNA ROLES A LOS USUARIOS
INSERT INTO users_roles (user_id, role_id) VALUES
(1,4),
(2,3),
(3,2);

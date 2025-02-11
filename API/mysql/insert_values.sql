-- AÑADE LOS IDIOMAS EN LA BASE DE DATOS
INSERT INTO languages (lang_code, language) VALUES
('esp', 'Español'),
('eng', 'English'),
('fra', 'Français'),
('deu', 'Deutsch'),
('ita', 'Italiano');

-- AÑADE LOS PERMISOS EXISTENTES EN LA BASE DE DATOS
INSERT INTO permission(name) VALUES
("CREATE"),
("DELETE"),
("UPDATE"),
("READ");

-- AÑADE LOS ROLES EXISTENTES EN LA BASE DE DATOS
INSERT INTO roles(role_name) VALUES
("GUESS"),
("USER"),
("MODERATOR"),
("ADMIN");

-- CREA RELACIONES ENTRE ROLES Y PERMISOS
INSERT INTO roles_permissions(role_id, permission_id) VALUES
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
INSERT INTO users(credential_no_locked, account_no_expired, account_no_locked, username, is_enabled, password) VALUES
(true, true, true, 'gagochorenmartin@gmail.com', true,'12345'), -- admin
(true, true, true, 'chgnitram@gmail.com', true,'12345'), -- moderator
(true, true, true, 'test@test.com', true, '12345'); -- user

-- ASIGNA ROLES A LOS USUARIOS
INSERT INTO users_roles(user_id, role_id) VALUES
(1,4),
(2,3),
(3,2)





DROP TABLE IF EXISTS relations_batch;
DROP TABLE IF EXISTS examples_batch;
DROP TABLE IF EXISTS definitions_batch;
DROP TABLE IF EXISTS words_batch;

DROP TABLE IF EXISTS words_examples;
DROP TABLE IF EXISTS words_relations;
DROP TABLE IF EXISTS words_definitions;
DROP TABLE IF EXISTS words;
DROP TABLE IF EXISTS words_qualifications;

DROP TABLE IF EXISTS api_words.languages;
-- BATCH TEST TABLES

DELETE FROM words_examples;
DELETE FROM words_relations;
DELETE FROM words_definitions;
DELETE FROM words;
DELETE FROM words_qualifications;


-- ROLES TABLES
DROP TABLE IF EXISTS api_words.users_roles;
DROP TABLE IF EXISTS api_words.roles_permissions;
DROP TABLE IF EXISTS api_words.roles;
DROP TABLE IF EXISTS api_words.permission;
DROP TABLE IF EXISTS api_words.users;

-- RESTORE DATA
DELETE FROM words_qualifications;
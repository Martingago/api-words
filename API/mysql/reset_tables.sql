DROP TABLE IF EXISTS api_words.words_examples;
DROP TABLE IF EXISTS api_words.words_relations;
DROP TABLE IF EXISTS api_words.words_definitions;
DROP TABLE IF EXISTS api_words.words;
DROP TABLE IF EXISTS api_words.languages;
DROP TABLE IF EXISTS api_words.words_qualifications;

-- BATCH TEST TABLES

DROP TABLE IF EXISTS relations_batch;
DROP TABLE IF EXISTS examples_batch;
DROP TABLE IF EXISTS definitions_batch;
DROP TABLE IF EXISTS words_batch;

-- ROLES TABLES
DROP TABLE IF EXISTS api_words.users_roles;
DROP TABLE IF EXISTS api_words.roles_permissions;
DROP TABLE IF EXISTS api_words.roles;
DROP TABLE IF EXISTS api_words.permission;
DROP TABLE IF EXISTS api_words.users;

-- RESTORE DATA
DELETE FROM words_qualifications;
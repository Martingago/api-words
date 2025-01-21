INSERT INTO languages (lang_code, language) VALUES
('esp', 'Español'),
('eng', 'English'),
('fra', 'Français'),
('deu', 'Deutsch'),
('ita', 'Italiano');

INSERT INTO words_qualifications(qualification) VALUES
('sustantivo masculino'),
('sustantivo femenino'),
('adjetivo femenino');

INSERT INTO words(word, word_length, id_language) VALUES
("roca",4, 1),
("piedra", 5, 1),
("blando", 6, 1);

INSERT INTO words_definitions(word_definition, id_word, id_qualification) VALUES
("Piedra, o vena de ella, muy dura y sólida.", 1, 1),
("Peñasco que se levanta en la tierra o en el mar.", 1, 1),
("Cosa muy dura, firme y constante.", 1, 1),
("Sustancia mineral, más o menos dura y compacta.", 2, 2),
("Trozo de piedra que se usa en la construcción.", 2, 2),
("Que cede fácilmente a la presión del tacto.", 3, 1);

INSERT INTO words_examples(example, id_definition) VALUES
("Le cayó una roca en el cráneo", 1),
("Estaba duro como una roca", 1),
("La roca pesaba 5 toneladas", 1),
("Me gustan mucho las películas de la roca", 3),
("Se puso tan fuerte como la roca", 3);

INSERT INTO words_relations(relation, id_definition, id_word) VALUES
("SINONIMA", 1, 2),
("SINONIMA",2, 2),
("ANTONIMA", 6, 1),
("ANTONIMA", 1, 3);



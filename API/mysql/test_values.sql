SELECT COUNT(*) FROM words where length = 4;
SELECT COUNT(*) FROM words_definitions;
SELECT COUNT(*) FROM words_examples;
SELECT COUNT(*) FROM words_relations where relation = 'ANTONIMA';

SELECT id, length FROM words WHERE is_placeholder = false;

SELECT id FROM words WHERE words.is_placeholder = false AND words.length = 4 ORDER BY RANDOM() LIMIT 1;

SELECT wr.*
FROM words_relations wr
LEFT JOIN words w ON w.id = wr.id_word
WHERE w.id IS NULL;


SELECT * FROM words_batch where word = 'aguazur';
SELECT * FROM definitions_batch where word_id = 259167;

SELECT COUNT(*) FROM words_batch;

SELECT word, COUNT(*) as count
FROM words_batch
GROUP BY word
HAVING COUNT(*) > 1;

SELECT COUNT(*) as repeated_words_count
FROM (
    SELECT word
    FROM words_batch
    GROUP BY word
    HAVING COUNT(*) > 1
) AS repeated_words;
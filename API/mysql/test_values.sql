SELECT COUNT(*) FROM words_batch where is_placeholder = false;

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
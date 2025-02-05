package com.martingago.words.batch;

import com.martingago.words.model.WordModel;
import com.martingago.words.repository.WordRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class WordItemWriter implements ItemWriter<WordModel> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public WordItemWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void write(Chunk<? extends WordModel> chunk) throws Exception {
        // Preparar un batch de inserciones
        jdbcTemplate.batchUpdate(
                "INSERT INTO words (word, id_language, word_length, is_placeholder) VALUES (?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        WordModel word = words.get(i);
                        ps.setString(1, word.getWord());
                        ps.setLong(2, word.getLanguageModel().getId());
                        ps.setInt(3, word.getWordLength());
                        ps.setBoolean(4, word.isPlaceholder());
                    }

                    @Override
                    public int getBatchSize() {
                        return words.size();
                    }
                }
        );
    }
}

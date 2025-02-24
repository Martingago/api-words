package com.martingago.words.repository;

import com.martingago.words.dto.global.WordStaticsDTO;
import com.martingago.words.model.WordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StaticsRepository extends  JpaRepository<WordModel, Long>{

    @Query(value = "SELECT" +
            "(SELECT COUNT(*) FROM words) AS words_count," +
            "(SELECT COUNT(*) FROM words_definitions) AS words_definitions_count," +
            "(SELECT COUNT(*) FROM words_examples) AS words_examples_count," +
            "(SELECT COUNT(*) FROM words_relations where relation = 'SINONIMA') AS words_synonyms_count;",
            nativeQuery = true)
    public WordStaticsDTO getStaticsFromDatabase();
}

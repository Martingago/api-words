package com.martingago.words.domain.repository;

import com.martingago.words.dto.global.stats.WordStatsDTO;
import com.martingago.words.domain.model.WordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StatsRepository extends  JpaRepository<WordModel, Long>{

    @Query(value = "SELECT" +
            "(SELECT COUNT(*) FROM words) AS words_count," +
            "(SELECT COUNT(*) FROM words_qualifications) AS words_qualifications_count," +
            "(SELECT COUNT(*) FROM words_definitions) AS words_definitions_count," +
            "(SELECT COUNT(*) FROM words_examples) AS words_examples_count," +
            "(SELECT COUNT(*) FROM words_relations where relation = 'SINONIMA') AS words_synonyms_count," +
            "(SELECT COUNT(*) FROM words_relations where relation = 'ANTONIMA') AS words_antonyms_count;",
            nativeQuery = true)
    public WordStatsDTO getStaticsFromDatabase();
}

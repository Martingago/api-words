package com.martingago.words.repository;

import com.martingago.words.model.WordModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.Set;

@Repository
public interface WordRepository extends JpaRepository<WordModel, Long> {

    Optional<WordModel> findByWord(String word);

    Set<WordModel> findByWordIn(Set<String> wordStringSet);

    @Query("SELECT DISTINCT w FROM WordModel w " +
            "JOIN FETCH w.languageModel " +
            "LEFT JOIN FETCH w.wordDefinitionModelSet wd " +
            "LEFT JOIN FETCH wd.wordQualificationModel " +
            "LEFT JOIN FETCH wd.wordExampleModelSet " +
            "LEFT JOIN FETCH wd.wordRelationModelSet wr " +
            "LEFT JOIN FETCH wr.wordRelated " +
            "WHERE w.word = :word")
    Optional<WordModel> findByWordWithRelations(@Param("word") String word);


    @EntityGraph(attributePaths = {
            "languageModel",
            "wordDefinitionModelSet.wordQualificationModel",
            "wordDefinitionModelSet.wordExampleModelSet",
            "wordDefinitionModelSet.wordRelationModelSet.wordRelated"
    })
    @Query("SELECT w FROM WordModel w " +
            "JOIN w.languageModel l " +
            "WHERE l.langCode = :langCode " +
            "ORDER BY FUNCTION('RAND') LIMIT 1")
    Optional<WordModel> findRandomWord(@Param("langCode") String langCode);


}

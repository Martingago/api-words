package com.martingago.words.repository;

import com.martingago.words.model.WordModel;
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

    @Query("SELECT DISTINCT w FROM WordModel w " +
            "JOIN FETCH w.languageModel " +
            "LEFT JOIN FETCH w.wordDefinitionModelSet wd " +
            "LEFT JOIN FETCH wd.wordQualificationModel " +
            "LEFT JOIN FETCH wd.wordExampleModelSet " +
            "LEFT JOIN FETCH wd.wordRelationModelSet wr " +
            "LEFT JOIN FETCH wr.wordRelated " +
            "WHERE w.word = :word and w.languageModel.langCode = :lang")
    Optional<WordModel> findByWordWithRelationsByLanguage(@Param("word") String word, @Param("lang") String lang);


    @Query(value = "SELECT id FROM words " +
            "WHERE words.is_placeholder = false " + //placeholder
            "AND (:wordLength IS NULL OR words.word_length = :wordLength) " + //longitud carácteres palabra
            "ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Long findRandomWordId(@Param("wordLength") Integer wordLength);

    @Query("SELECT DISTINCT w FROM WordModel w " +
            "JOIN FETCH w.languageModel " +
            "LEFT JOIN FETCH w.wordDefinitionModelSet wd " +
            "LEFT JOIN FETCH wd.wordQualificationModel " +
            "LEFT JOIN FETCH wd.wordExampleModelSet " +
            "LEFT JOIN FETCH wd.wordRelationModelSet wr " +
            "LEFT JOIN FETCH wr.wordRelated " +
            "WHERE w.id = :idWord")
    Optional<WordModel> findById(@Param("idWord")Long id);


}

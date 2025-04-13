package com.martingago.words.domain.repository;

import com.martingago.words.dto.word.request.WordBatchReferenceDTO;
import com.martingago.words.domain.model.WordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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


    /**
     * Obtiene un ID aleatorio de una palabra con un tamaño determinado
     * @param wordLength tamaño de la palabra que se quiere obtener aleatoriamente
     *                   Si no se proporciona un tamaño, se obtendrá un valor aleatorio
     * @return Long ID de la palabra obtenida aleatoriamente.
     */
    @Query(value = "SELECT id FROM words " +
            "WHERE words.is_placeholder = false " + //placeholder
            "AND (:wordLength IS NULL OR words.length = :wordLength) " + //longitud carácteres palabra
            "ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Long findRandomWordId(@Param("wordLength") Integer wordLength);


    @Query("SELECT DISTINCT w FROM WordModel w " +
            "JOIN FETCH w.languageModel " +
            "LEFT JOIN FETCH w.wordDefinitionModelSet wd " +
            "LEFT JOIN FETCH wd.wordQualificationModel " +
            "LEFT JOIN FETCH wd.wordExampleModelSet " +
            "LEFT JOIN FETCH wd.wordRelationModelSet wr " +
            "LEFT JOIN FETCH wr.wordRelated " +
            "WHERE w.id = :idWord")
    Optional<WordModel> findWordById(@Param("idWord")Long id);


    @Query("SELECT new com.martingago.words.dto.word.request.WordBatchReferenceDTO(w.id, w.word, w.isPlaceholder) " +
            "FROM WordModel w " +
            "WHERE w.word IN :words")
    List<WordBatchReferenceDTO> findReferencesByWordIn(@Param("words") Set<String> words);


    @Query("SELECT w FROM WordModel w " +
            "WHERE w.word IN :words")
    Set<WordModel> findWordAndLanguageIn(@Param("words") Set<String> words);

}

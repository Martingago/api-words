package com.martingago.words.domain.repository.models;

import com.martingago.words.domain.model.WordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface WordRepository extends JpaRepository<WordModel, Long> {

    Set<WordModel> findByWordIn(Set<String> wordStringSet);

    /**
     * Busca en la base de datos los detalles de una palabra específicada.
     * @param word string de la palabra que se quiere buscar.
     * @return optional de WordModel con la información de la palabra encontrada.
     */
    @Query("SELECT DISTINCT w FROM WordModel w " +
            "JOIN FETCH w.languageModel " +
            "LEFT JOIN FETCH w.wordDefinitionModelSet wd " +
            "LEFT JOIN FETCH wd.wordQualificationModel " +
            "LEFT JOIN FETCH wd.wordExampleModelSet " +
            "LEFT JOIN FETCH wd.wordRelationModelSet wr " +
            "LEFT JOIN FETCH wr.wordRelated " +
            "WHERE w.word = :word")
    Optional<WordModel> findByWordWithRelations(@Param("word") String word);


    /**
     * Obtiene el WordModel encontrado de una palabra que tiene un idioma especificado
     * @param word palabra que se quiere buscar
     * @param lang código de idioma que debe tener la palabra
     * @return Optional WordModel con la palabra encontrada.
     */
    @Query("SELECT DISTINCT w FROM WordModel w " +
            "JOIN FETCH w.languageModel wl " +
            "WHERE w.word = :word and wl.langCode = :lang")
    Optional<WordModel> findByWordAndLanguage(@Param("word") String word, @Param("lang") String lang);


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


    /**
     * Obtiene la información global de una palabra bajo una única consulta SQL
     * @param id identificador de la palabra de la que se quieren extraer los datos
     * @return WordModel con toda la información existente de una palabra.
     */
    @Query("SELECT DISTINCT w FROM WordModel w " +
            "JOIN FETCH w.languageModel " +
            "LEFT JOIN FETCH w.wordDefinitionModelSet wd " +
            "LEFT JOIN FETCH wd.wordQualificationModel " +
            "LEFT JOIN FETCH wd.wordExampleModelSet " +
            "LEFT JOIN FETCH wd.wordRelationModelSet wr " +
            "LEFT JOIN FETCH wr.wordRelated " +
            "WHERE w.id = :idWord")
    Optional<WordModel> findWordById(@Param("idWord")Long id);

    @Query("SELECT w.word FROM WordModel w WHERE w.word IN :words")
    Set<String> findWordsByWordIn(@Param("words") Set<String> words);

    @Query("SELECT w from WordModel w WHERE w.word IN :words")
    Set<WordModel> findWordModelIn(@Param("words") Set<String> words);

}

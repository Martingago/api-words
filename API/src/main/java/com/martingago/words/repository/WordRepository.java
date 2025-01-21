package com.martingago.words.repository;

import com.martingago.words.model.WordModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<WordModel, Long> {

    Optional<WordModel> findByWord(String word);

    Optional<WordModel> findById(Long id);



    //Genera una palabra aleatoria de la base de datos
    @Query(value = "SELECT * FROM word_model  " +
            "WHERE language = 'esp' ORDER BY RAND() LIMIT 1", nativeQuery = true)
    WordModel findRandomWord();

    //Booleano que comprueba si una palabra existe o no en la Base de datos
    Boolean existsByWord(String word);

    /**
     * Comprueba de una lista de palabras aquellas que existen en la Base de datos
     * @param words listado de palabras a validar si existen o no en la Base de datos
     * @return List<String> de palabras que si existen en la base de datos.
     */
    @Query("SELECT w.word FROM WordModel w WHERE w.word IN :words")
    List<String> findExistingWords(List<String> words);

    /**
     * Elimina un conjunto de palabras de la base de dato
     * @param words List de palabras que se quieren eliminar de la base de datos.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM WordModel w WHERE w.word IN :words")
    void deleteAllByWordIn(List<String> words);
}

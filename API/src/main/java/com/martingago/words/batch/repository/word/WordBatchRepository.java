package com.martingago.words.batch.repository.word;

import com.martingago.words.batch.dto.WordBatchReferenceDTO;
import com.martingago.words.batch.model.WordBatch;
import com.martingago.words.model.WordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface WordBatchRepository extends JpaRepository<WordBatch, Long> {
    /**
     * query para filtrar aquellas palabras que existen en la BBDD y no sean placeholders para evitar duplicados
     * @param words
     * @return
     */
    @Query("SELECT w.word FROM WordBatch w WHERE w.word IN :words AND w.isPlaceholder = false")
    List<String> findExistingNonPlaceholderWords(List<String> words);

    @Query("SELECT new com.martingago.words.batch.dto.WordBatchReferenceDTO(w.id, w.word, w.isPlaceholder, w.language.id) FROM WordBatch w WHERE w.word IN :words")
    List<WordBatchReferenceDTO> findReferencesByWordIn(@Param("words") Set<String> words);


    Set<WordBatch> findByWordIn(Set<String> words);

    WordBatch findByWord(String word);
}

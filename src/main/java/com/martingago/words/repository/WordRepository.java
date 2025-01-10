package com.martingago.words.repository;

import com.martingago.words.model.WordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends JpaRepository<WordModel, Long> {

    @Query(value = "SELECT * FROM word_model ORDER BY RAND() LIMIT 1", nativeQuery = true)
    WordModel findRandomWord();

    Boolean existsByWord(String word);
}

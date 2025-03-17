package com.martingago.words.batch;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "word_batch")
public class WordBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "word_entity_seq")
    @SequenceGenerator(name = "word_entity_seq", sequenceName = "word_entity_seq")
    private long id;

    private String word;

    private String languageModel; //Idioma al que está asociada la palabra

    private int wordLength; //Longitud de la palabra

    private boolean isPlaceholder;

}

@Repository
interface WordBatchRepository extends JpaRepository<WordBatch, Long> {
}

package com.martingago.words.batch;

import com.martingago.words.model.LanguageModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "word_batch")
public class WordBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "word_batch_seq")
    @SequenceGenerator(name = "word_batch_seq", sequenceName = "word_batch_seq", allocationSize = 100)
    private long id;

    private String word;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_language", nullable = false)
    private LanguageModel language; //Idioma al que está asociada la palabra

    private int length; //Longitud de la palabra

    private boolean isPlaceholder;

}


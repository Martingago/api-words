package com.martingago.words.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "words_examples")
public class WordExampleModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "word_example_entity_seq")
    @SequenceGenerator(name = "word_example_entity_seq", sequenceName = "word_example_entity_seq")
    private Long id;

    private String example;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_definition", nullable = false) // Clave foránea
    private WordDefinitionModel wordDefinitionModel; // Relación bidireccional
}

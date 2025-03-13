package com.martingago.words.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "words")
public class WordModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "word_entity_seq")
    @SequenceGenerator(name = "word_entity_seq", sequenceName = "word_entity_seq", allocationSize = 100)
    private long id;

    @Column(name = "word", unique = true)
    private String word;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_language", nullable = false)
    private LanguageModel languageModel; //Idioma al que está asociada la palabra

    private int wordLength; //Longitud de la palabra

    @OneToMany(mappedBy = "word", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WordDefinitionModel> wordDefinitionModelSet = new HashSet<>(); //Listado de definiciones que puede tener una palabra

    private boolean isPlaceholder;
}

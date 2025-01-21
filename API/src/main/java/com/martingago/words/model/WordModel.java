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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String word;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_language", nullable = false)
    private LanguageModel languageModel; //Idioma al que está asociada la palabra

    private int wordLength; //Longitud de la palabra

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_word")
    private Set<WordDefinitionModel> wordDefinitionModelSet = new HashSet<>(); //Listado de definiciones que puede tener una palabra

}

package com.martingago.words.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
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
    private LanguageModel languageModel;

    private int wordLength;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_word")
    private List<WordDefinitionModel> wordDefinitionModelList;

}

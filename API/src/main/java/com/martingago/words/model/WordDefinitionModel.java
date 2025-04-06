package com.martingago.words.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "words_definitions")
public class WordDefinitionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "word_def_entity_seq")
    @SequenceGenerator(name = "word_def_entity_seq", sequenceName = "word_def_entity_seq")
    private Long id;

    @Column(length = 5000)
    private String wordDefinition; //Definición de la palabra

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_word", nullable = false)
    private WordModel word; //Relación bidireccional para obtener información de la palabra a la que está asociada la definición.

    @OneToMany(mappedBy = "wordDefinitionModel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WordExampleModel> wordExampleModelSet = new HashSet<>(); // Listado de ejemplos que puede tener una palabra

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_qualification")
    private WordQualificationModel wordQualificationModel; // Clasificación a la que está asociada una definición de palabra. Ej: "Sustantivo masculino"

    @OneToMany(mappedBy = "wordDefinitionModel", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<WordRelationModel> synonymRelationsSet = new HashSet<>();

    @OneToMany(mappedBy = "wordDefinitionModel", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<WordRelationModel> antonymRelationsSet = new HashSet<>();

}

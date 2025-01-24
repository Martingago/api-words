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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String wordDefinition; //Definición de la palabra

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_word", nullable = false)
    private WordModel word; //Relación bidireccional para obtener información de la palabra a la que está asociada la definición.

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_definition")
    private Set<WordExampleModel> wordExampleModelSet = new HashSet<>(); // Listado de ejemplos que puede tener una palabra

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_qualification")
    private WordQualificationModel wordQualificationModel; // Clasificación a la que está asociada una definición de palabra. Ej: "Sustantivo masculino"

    @OneToMany(mappedBy = "wordDefinitionModel", fetch = FetchType.LAZY)
    private Set<WordRelationModel> wordRelationModelSet = new HashSet<>(); //Listado de relaciones con otras palabras que tiene una definición: SINONIMA/ANTONIMA

}

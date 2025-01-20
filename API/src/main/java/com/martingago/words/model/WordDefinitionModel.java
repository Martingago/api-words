package com.martingago.words.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
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
    private List<WordExampleModel> wordExampleModelList; // Listado de ejemplos que puede tener una palabra

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_qualification")
    private WordQualificationModel wordQualificationModel; // Clasificación a la que está asociada una definición de palabra. Ej: "Sustantivo masculino"


}

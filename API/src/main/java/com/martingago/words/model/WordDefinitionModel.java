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

    private String wordDefinition;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_definition")
    private List<WordExampleModel> wordExampleModelList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_qualification")
    private WordQualificationModel wordQualificationModel;

}

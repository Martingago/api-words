package com.martingago.words.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "words_relations")
public class WordRelationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_definition", nullable = false)
    private WordDefinitionModel wordDefinitionModel; //FK hacia la definicion de palabra con la que tiene relacion

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_word", nullable = false)
    private WordModel wordRelated; //FK hacia la palabra con la que tiene la relacion

    @Enumerated(EnumType.STRING)
    @Column(name = "relation", nullable = false)
    private RelationEnumType relationEnumType; //Enum tipos relacion: SINONIMA o ANTONIMA
}

package com.martingago.words.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
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
    private WordDefinitionModel wordDefinitionModel; //FK hacia la definición de palabra con la que tiene relación

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_word", nullable = false)
    private WordModel wordRelated; //FK hacia la palabra con la que tiene la relación

    @Enumerated(EnumType.STRING)
    @Column(name = "relation", nullable = false)
    private RelationEnumType relationEnumType; //Enum tipos relación: SINÓNIMA o ANTÓNIMA.
}

package com.martingago.words.batch.model;

import com.martingago.words.model.RelationEnumType;
import com.martingago.words.model.WordDefinitionModel;
import com.martingago.words.model.WordModel;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "relations_batch")
public class RelationBatch implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "relation_entity_seq")
    @SequenceGenerator(name = "relation_entity_seq", sequenceName = "relation_entity_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_definition", nullable = false)
    private DefinitionBatch definitionBatch; //FK hacia la definición de palabra con la que tiene relación

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_word", nullable = false)
    private WordBatch wordRelated; //FK hacia la palabra con la que tiene la relación

    @Enumerated(EnumType.STRING)
    @Column(name = "relation", nullable = false)
    private RelationEnumType relationEnumType; //Enum tipos relación: SINÓNIMA o ANTÓNIMA.
}

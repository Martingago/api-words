package com.martingago.words.batch.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "examples_batch")
public class ExampleBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "example_seq")
    @SequenceGenerator(name = "example_seq", sequenceName = "example_seq")
    private Long id;

    private String example;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_definition", nullable = false) // Clave foránea
    private DefinitionBatch definitionBatch; // Relación bidireccional
}

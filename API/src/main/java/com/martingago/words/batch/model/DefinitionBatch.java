package com.martingago.words.batch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "definitions_batch")
public class DefinitionBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "definition_seq")
    @SequenceGenerator(name = "definition_seq", sequenceName = "definition_seq", allocationSize = 100)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private WordBatch word;

    @Column(length = 5000)
    private String definition;
}

package com.martingago.words.batch.model;

import com.martingago.words.model.WordExampleModel;
import com.martingago.words.model.WordQualificationModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "definitions_batch")
public class DefinitionBatch implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "definition_seq")
    @SequenceGenerator(name = "definition_seq", sequenceName = "definition_seq", allocationSize = 100)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private WordBatch word;

    @Column(length = 5000)
    private String definition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_qualification")
    private WordQualificationModel wordQualificationModel; // Clasificación a la que está asociada una definición de palabra. Ej: "Sustantivo masculino"

    @OneToMany(mappedBy = "definitionBatch", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ExampleBatch> examples = new HashSet<>(); // Listado de ejemplos que puede tener una palabra

    @OneToMany(mappedBy = "definitionBatch", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RelationBatch> synonymRelations = new HashSet<>();

    @OneToMany(mappedBy = "definitionBatch", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RelationBatch> antonymRelations = new HashSet<>();
}

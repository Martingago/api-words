package com.martingago.words.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "words_qualifications")
public class WordQualificationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "word_qualification_entity_seq")
    @SequenceGenerator(name = "word_qualification_entity_seq", sequenceName = "word_qualification_entity_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false)
    private String qualification;
}

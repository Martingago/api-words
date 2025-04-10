package com.martingago.words.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "words_qualifications")
public class WordQualificationModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "word_qualification_entity_seq")
    @SequenceGenerator(name = "word_qualification_entity_seq", sequenceName = "word_qualification_entity_seq")
    private Long id;

    @Column(unique = true, nullable = false)
    private String qualification;
}

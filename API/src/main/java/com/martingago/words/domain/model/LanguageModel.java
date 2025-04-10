package com.martingago.words.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "languages")
public class LanguageModel implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lang_entity_seq")
    @SequenceGenerator(name = "lang_entity_seq", sequenceName = "lang_entity_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true)
    private  String langCode;

    private String language;
}

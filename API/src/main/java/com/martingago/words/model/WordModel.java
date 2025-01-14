package com.martingago.words.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WordModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String word;

    private String language;

    private int wordLength;

    @Column(columnDefinition = "TEXT")
    private String definition;

    private String qualification;
}

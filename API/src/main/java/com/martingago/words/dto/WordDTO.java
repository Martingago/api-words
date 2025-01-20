package com.martingago.words.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordDTO {

    private String word;
    private int length;
    private LanguageDTO languageDTO;
}

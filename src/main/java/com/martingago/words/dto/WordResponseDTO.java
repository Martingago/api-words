package com.martingago.words.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WordResponseDTO {
    @NotEmpty(message = "Word cannot be empty")
    private String word;

    @NotEmpty(message = "Language cannot be empty")
    private String language;

    private int wordLength;

    private String definition;
}

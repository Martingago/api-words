package com.martingago.words.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordDTO {

    private String language;
    private String word;
    private int length;
    private List<WordDefinitionDTO> definitions;
}

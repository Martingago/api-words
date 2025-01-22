package com.martingago.words.dto;

import com.martingago.words.model.LanguageModel;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordDTO {

    private String language;
    private LanguageModel languageModel; //Language Model usado para insertar words
    private String word;
    private int length;
    private Set<WordDefinitionDTO> definitions;
}

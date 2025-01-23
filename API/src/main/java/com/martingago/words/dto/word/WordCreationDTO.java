package com.martingago.words.dto.word;

import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.model.LanguageModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordCreationDTO {
    private LanguageModel languageModel;
    private String word;
    private int length;
    private Set<WordDefinitionDTO> definitions;
}

package com.martingago.words.dto.word;

import com.martingago.words.dto.WordDefinitionDTO;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordResponseDTO {
    private String language;
    private String word;
    private int length;
    private Set<WordDefinitionDTO> definitions;
}

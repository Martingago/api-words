package com.martingago.words.dto.word;

import com.martingago.words.dto.WordDefinitionDTO;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

/**
 * Clase que contiene los datos de una palabra y que serán enviados al front-end
 */
public class WordResponseViewDTO {
    private String language;
    private String word;
    private int length;
    private Set<WordDefinitionDTO> definitions;
}

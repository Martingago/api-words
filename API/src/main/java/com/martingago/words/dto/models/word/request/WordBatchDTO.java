package com.martingago.words.dto.models.word.request;

import com.martingago.words.dto.models.definition.WordDefinitionDTO;
import lombok.*;

import java.util.List;


/**
 * DTO que contiene la información que se recibe desde el front-end para cargar los datos
 */
@Data
public class WordBatchDTO {
    private String word;
    private String language; // Código del idioma, como "esp"
    private int length;
    private boolean isPlaceholder;
    private List<WordDefinitionDTO> definitions;
}


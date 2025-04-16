package com.martingago.words.dto.models.word.request;

import com.martingago.words.dto.models.definition.WordDefinitionDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Set;

/**
 * DTO que contiene la información que se recibe desde el front-end para cargar los datos
 */
@Data
@Schema(description = "Objeto que contiene la información de una palabra que se quiere subir a la base de datos")
public class WordBatchDTO {
    private String word;
    private String language; // Código del idioma, como "esp"
    private int length;
    private Set<WordDefinitionDTO> definitions;
}


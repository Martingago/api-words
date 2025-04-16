package com.martingago.words.dto.models.word;

import com.martingago.words.dto.models.definition.WordDefinitionDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

/**
 * Clase que contiene la información de una palabra
 */
@Schema(description = "Objeto que contiene la información de una palabra")
public class WordDTO {

    @Schema(description = "String con el código de idioma de la palabra", example= "esp")
    private String language;

    @Schema(description = "String con el texto de la palabra", example = "duro")
    private String word;

    @Schema(description = "Integer con la longitud de carácteres de la palabra", example = "4")
    private int length;

    @Schema(description = "Listado de objetos de definiciones de una palabra")
    private Set<WordDefinitionDTO> definitions;
}

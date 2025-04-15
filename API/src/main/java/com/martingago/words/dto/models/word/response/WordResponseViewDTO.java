package com.martingago.words.dto.models.word.response;

import com.martingago.words.dto.models.definition.WordDefinitionDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

/**
 * Clase que contiene los datos de una palabra que se envían al front-end
 */
@Schema(description = "Objeto que contiene la información de una palabra")
public class WordResponseViewDTO {

    @Schema(description = "String con el código de idioma de la palabra")
    private String language;

    @Schema(description = "String con el texto de la palabra")
    private String word;

    @Schema(description = "Integer con la longitud de carácteres de la palabra")
    private int length;

    @Schema(description = "Listado de objetos 'WordDefinitionDTO' de definiciones de una palabra")
    private Set<WordDefinitionDTO> definitions;
}

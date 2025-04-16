package com.martingago.words.dto.microservices.word.external;

import com.martingago.words.dto.models.definition.WordDefinitionDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
/**
 * DTO que representa una palabra completa recibida desde un microservicio.
 * Contiene todos los atributos necesarios para crear o actualizar una palabra
 * en la base de datos a partir de la información procesada por un microservicio.
 */

@Schema(description = "Objeto que contiene la información de una palabra obtenida desde un micro-servicio")
public class WordDTOExternal extends ExternalBaseWordDTO {

    @Schema(description = "String con el código de idioma de la palabra")
    private String language;

    @Schema(description = "String con el texto de la palabra")
    private String word;

    @Schema(description = "Palabra raíz de la palabra original")
    private String baseWord;

    @Schema(description = "Integer con la longitud de carácteres de la palabra")
    private int length;

    @Schema(description = "Listado de objetos de definiciones de una palabra")
    private Set<WordDefinitionDTO> definitions;

}

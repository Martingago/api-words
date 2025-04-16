package com.martingago.words.dto.models.definition;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto que contiene la definición  y otros atributos de una palabra")
public class WordDefinitionDTO {

    @Schema(description = "String con la clasificación de una palabra.",
            example ="adjetivo")
    private String qualification;

    @Schema(description = "String con la definición de una palabra." ,
            example = "Fuerte, que resiste y soporta bien la fatiga.")
    private String definition;

    @Schema(description = "Listado de ejemplos (String) de uso de la palabra",
            example = "[\"Es duro como una roca\", \"Trabaja duro\"]")
    private Set<String> examples;

    @Schema(description = "Listado de palabras sinónimas (String) a la definición",
            example = "[\"fuerte\", \"tenaz\", \"resistente\"]")
    private Set<String> synonyms;

    @Schema(description = "Listado de palabras antónimas (String) a la definición",
            example= "[\"blando\"]")
    private Set<String> antonyms;
}

package com.martingago.words.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto que contiene la definición  y otros atributos de una palabra")
public class WordDefinitionDTO {

    @Schema(description = "String con la clasificación de una palabra.")
    private String qualification;

    @Schema(description = "String con la definición de una palabra.")
    private String definition;

    @Schema(description = "Listado de ejemplos (String) de uso de la palabra")
    private Set<String> examples;

    @Schema(description = "Listado de palabras sinónimas (String) a la definición")
    private Set<String> synonyms;

    @Schema(description = "Listado de palabras antónimas (String) a la definición")
    private Set<String> antonyms;
}

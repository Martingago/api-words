package com.martingago.words.dto.models.word.request;

import com.martingago.words.dto.models.definition.WordDefinitionDTO;
import lombok.*;

import java.util.Set;

@Getter
@Setter

/**
 * Clase recibida desde el micro-servicio que contiene toda la información de una palabra procesada y que se
 * emplea para crear el objeto en la Base de datos.
 */
public class FullWordRequestDTO extends BaseWordRequestDTO {
    private String language;
    private String word;
    private String baseWord;
    private int length;
    private Set<WordDefinitionDTO> definitions;

}

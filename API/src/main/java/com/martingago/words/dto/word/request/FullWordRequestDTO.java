package com.martingago.words.dto.word.response;

import com.martingago.words.dto.WordDefinitionDTO;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter

/**
 * Clase recibida desde el micro-servicio que contiene toda la información de una palabra procesada y que se
 * emplea para crear el objeto en la Base de datos.
 */
public class FullWordResponseDTO extends BaseWordResponseDTO{
    private String language;
    private String word;
    private String baseWord;
    private int length;
    private Set<WordDefinitionDTO> definitions;

}

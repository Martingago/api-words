package com.martingago.words.dto.word.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

/**
 * Clase recibida desde el micro-servicio que contiene toda la información de una palabra que fue procesada pero
 * que en su lugar se ha encontrado una palabra relacionada.
 */
public class RelatedWordRequestDTO extends BaseWordRequestDTO {

    private String relatedWord;
}

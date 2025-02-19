package com.martingago.words.dto.word.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

/**
 * Clase que se emplea para scrapear palabras como objeto para enviar al micro-servicio de scrapping
 */
public class ScrapWordDTO {
    private String word;
}

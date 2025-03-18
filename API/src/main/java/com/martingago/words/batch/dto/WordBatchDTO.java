package com.martingago.words.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * DTO que contiene la información que se recibe desde el front-end para cargar los datos
 */
public class WordBatchDTO {
    private String word;
    private String language; // Código del idioma, como "esp"
    private int length;
    private boolean isPlaceholder;
}

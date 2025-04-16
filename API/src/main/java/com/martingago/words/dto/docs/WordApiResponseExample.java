package com.martingago.words.dto.docs;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.models.word.WordDTO;

/**
 * Clase que se usa como objeto de la documentación para devolver una palabra completa (palabra + definiciones).
 */
public class WordApiResponseExample extends ApiResponseDTO<WordDTO> {

    public WordApiResponseExample(boolean status, String message, int serverCode, WordDTO responseObject) {
        super(status, message, serverCode, responseObject);
    }

}

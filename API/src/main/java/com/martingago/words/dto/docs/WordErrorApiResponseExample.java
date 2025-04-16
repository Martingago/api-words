package com.martingago.words.dto.docs;

import com.martingago.words.dto.global.ApiResponseDTO;

/**
 * Clase que se usa como objeto de documentación para devolver los errores producidos en la ejecución de un enpoint
 */
public class WordErrorApiResponseExample extends ApiResponseDTO<Void> {
    protected WordErrorApiResponseExample(boolean status, String message, int serverCode, Void responseObject) {
        super(false, message, serverCode, responseObject);
    }
}

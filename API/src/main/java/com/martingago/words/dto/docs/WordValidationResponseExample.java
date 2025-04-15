package com.martingago.words.dto.docs;

import com.martingago.words.dto.global.ApiResponseDTO;

import java.util.Map;

/**
 * Clase que se usa como documentación para devolver las palabras validadas en la base de datos: palabra - boolean.
 */
public class WordValidationResponseExample extends  ApiResponseDTO<Map<String, Boolean>>{

    protected WordValidationResponseExample(boolean status, String message, int serverCode, Map<String, Boolean> responseObject) {
        super(status, message, serverCode, responseObject);
    }
}

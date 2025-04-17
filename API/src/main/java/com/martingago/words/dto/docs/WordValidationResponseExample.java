package com.martingago.words.dto.docs;

import com.martingago.words.dto.global.ApiResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

/**
 * Clase que se usa como documentación para devolver las palabras validadas en la base de datos: palabra - boolean.
 */
public class WordValidationResponseExample extends  ApiResponseDTO<Map<String, Boolean>>{

    @Schema(
            description = "Listado de palabras validadas con su estado de existencia.",
            example = "{\"perro\": true, \"gato\": true, \"tortuga\": true, \"pvksa\":false}"
    )
    private Map<String, Boolean> responseObject;

    protected WordValidationResponseExample(boolean status, String message, int serverCode, Map<String, Boolean> responseObject) {
        super(status, message, serverCode, responseObject);
    }
}

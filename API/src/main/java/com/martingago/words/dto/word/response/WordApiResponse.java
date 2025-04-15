package com.martingago.words.dto.word.response;

import com.martingago.words.dto.global.ApiResponseDTO;
import lombok.Builder;

import java.time.Instant;

/**
 * Clase que se usa como objeto de la documentación.
 */
public class WordApiResponse extends ApiResponseDTO<WordResponseViewDTO> {

    public WordApiResponse(boolean status, String message, int serverCode, WordResponseViewDTO responseObject) {
        super(status, message, serverCode, responseObject);
    }

}

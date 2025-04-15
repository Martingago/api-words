package com.martingago.words.dto.word.response;

import com.martingago.words.dto.global.ApiResponseDTO;

import java.util.Map;

public class WordValidationResponse extends  ApiResponseDTO<Map<String, Boolean>>{

    protected WordValidationResponse(boolean status, String message, int serverCode, Map<String, Boolean> responseObject) {
        super(status, message, serverCode, responseObject);
    }
}

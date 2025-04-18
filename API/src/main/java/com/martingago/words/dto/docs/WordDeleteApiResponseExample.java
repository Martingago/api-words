package com.martingago.words.dto.docs;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.models.word.DeleteWordRequestDTO;

public class WordDeleteApiResponseExample extends ApiResponseDTO<DeleteWordRequestDTO> {

    protected WordDeleteApiResponseExample(boolean status, String message, int serverCode, DeleteWordRequestDTO responseObject) {
        super(status, message, serverCode, responseObject);
    }
}

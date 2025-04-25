package com.martingago.words.dto.docs;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.models.language.LanguageDTO;

import java.util.List;

public class LanguageListApiResponseExample extends ApiResponseDTO<List<LanguageDTO>> {

    protected LanguageListApiResponseExample(boolean status, String message, int serverCode, List<LanguageDTO> responseObject) {
        super(status, message, serverCode, responseObject);
    }
}

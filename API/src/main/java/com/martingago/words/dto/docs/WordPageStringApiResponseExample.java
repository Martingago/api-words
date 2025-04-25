package com.martingago.words.dto.docs;

import com.martingago.words.dto.global.ApiResponseDTO;
import org.springframework.data.domain.Page;

public class WordPageStringApiResponseExample extends ApiResponseDTO<Page<String>> {

    protected WordPageStringApiResponseExample(boolean status, String message, int serverCode, Page<String> responseObject) {
        super(status, message, serverCode, responseObject);
    }
}

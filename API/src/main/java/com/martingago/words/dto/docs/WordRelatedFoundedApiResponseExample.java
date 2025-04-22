package com.martingago.words.dto.docs;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.microservices.word.external.RelatedWordDTOExternal;

public class WordRelatedFoundedApiResponseExample extends ApiResponseDTO<RelatedWordDTOExternal> {
    protected WordRelatedFoundedApiResponseExample(boolean status, String message, int serverCode, RelatedWordDTOExternal responseObject) {
        super(status, message, serverCode, responseObject);
    }
}

package com.martingago.words.dto.global;

import java.util.List;

public class ListStringApiResponse extends ApiResponseDTO<List<String>>{

    protected ListStringApiResponse(boolean status, String message, int serverCode, List<String> responseObject) {
        super(status, message, serverCode, responseObject);
    }
}

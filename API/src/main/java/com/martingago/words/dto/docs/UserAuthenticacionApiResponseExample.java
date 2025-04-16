package com.martingago.words.dto.docs;

import com.martingago.words.dto.authentication.AuthResponseDTO;
import com.martingago.words.dto.global.ApiResponseDTO;


public class UserAuthenticacionApiResponseExample extends ApiResponseDTO<AuthResponseDTO> {

    protected UserAuthenticacionApiResponseExample(boolean status, String message, int serverCode, AuthResponseDTO responseObject) {
        super(status, message, serverCode, responseObject);
    }
}

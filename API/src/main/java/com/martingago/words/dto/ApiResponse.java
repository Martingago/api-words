package com.martingago.words.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.Instant;

@Getter
public class ApiResponse<T> {
    private final boolean status;
    private final String message;
    private final Integer serverCode;
    private final T responseObject;
    private final Instant timeStamp;

    @Builder
    private ApiResponse(boolean status, String message, int serverCode, T responseObject) {
        this.status = status;
        this.message = message;
        this.serverCode = serverCode;
        this.responseObject = responseObject;
        this.timeStamp = Instant.now();
    }

    public static <T> ResponseEntity<ApiResponse<T>> build(boolean status, String message, Integer serverCode, T responseObject, HttpStatus httpStatus) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .responseObject(responseObject)
                .serverCode(serverCode)
                .build();

        return new ResponseEntity<>(response, httpStatus);
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String message, Integer errorCode, HttpStatus status) {
        return build(false, message, errorCode, null, status);
    }
}
package com.martingago.words.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class ApiResponse<T> {
    private final boolean status;
    private final String message;
    private final T responseObject;

    @Builder
    private ApiResponse(boolean status, String message, T responseObject) {
        this.status = status;
        this.message = message;
        this.responseObject = responseObject;
    }

    public static <T> ResponseEntity<ApiResponse<T>> build(boolean status, String message, T responseObject, HttpStatus httpStatus) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .responseObject(responseObject)
                .build();

        return new ResponseEntity<>(response, httpStatus);
    }

    // Métodos de conveniencia para respuestas comunes
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return build(true, "Success", data, HttpStatus.OK);
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String message) {
        return build(false, message, null, HttpStatus.BAD_REQUEST);
    }
}
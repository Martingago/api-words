package com.martingago.words.exceptions.domain;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.exceptions.microservice.WordGeneratedRelatedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
public class CustomExceptionsHandler {

    @ExceptionHandler(CustomExceptions.FileEmptyException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleFileEmpty(CustomExceptions.FileEmptyException e) {
        return ApiResponseDTO.error(e.getMessage(), 400, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomExceptions.NoValidWordsException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleNoWords(CustomExceptions.NoValidWordsException e) {
        return ApiResponseDTO.error(e.getMessage(), 400, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomExceptions.WordLimitExceededException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleWordLimit(CustomExceptions.WordLimitExceededException e) {
        return ApiResponseDTO.error(e.getMessage(), 413, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(CustomExceptions.UnsupportedFileTypeException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleUnsupportedFile(CustomExceptions.UnsupportedFileTypeException e) {
        return ApiResponseDTO.error(e.getMessage(), 415, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(WordGeneratedRelatedException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleWordRelatedGenerated(WordGeneratedRelatedException ex){
        return ApiResponseDTO.build(false, ex.getMessage(), 422, ex.getErrorObject(), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}

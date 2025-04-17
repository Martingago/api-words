package com.martingago.words.exceptions;

import com.martingago.words.dto.global.ApiResponseDTO;
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

    @ExceptionHandler(CustomExceptions.UnsupportedFileTypeException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleUnsupportedFile(CustomExceptions.UnsupportedFileTypeException e) {
        return ApiResponseDTO.error(e.getMessage(), 400, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomExceptions.WordLimitExceededException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleWordLimit(CustomExceptions.WordLimitExceededException e) {
        return ApiResponseDTO.error(e.getMessage(), 400, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomExceptions.NoValidWordsException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleNoWords(CustomExceptions.NoValidWordsException e) {
        return ApiResponseDTO.error(e.getMessage(), 400, HttpStatus.BAD_REQUEST);
    }
}

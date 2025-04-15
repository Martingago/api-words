package com.martingago.words.exceptions;

import com.martingago.words.dto.global.ApiResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleNotFoundEntity(EntityNotFoundException e){
        return ApiResponseDTO.error(e.getMessage(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = IOException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleIOException(IOException ex) {
        return ApiResponseDTO.error(ex.getMessage(), 500, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = DuplicateKeyException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleDuplicateKey(DuplicateKeyException e){
        return ApiResponseDTO.error(e.getMessage(), HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleIllegalArgument(IllegalArgumentException e){
        return  ApiResponseDTO.error(e.getMessage(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = BadRequestException.class)
    public  ResponseEntity<ApiResponseDTO<Object>> handleBadRequestException(BadRequestException e){
        return ApiResponseDTO.error(e.getMessage(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public  ResponseEntity<ApiResponseDTO<Object>> handleException(Exception e){
        return  ApiResponseDTO.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Excepciones del microservicio de scrapping
    @ExceptionHandler(ScrapingServiceException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleScrapingError(ScrapingServiceException e) {
        HttpStatus status = HttpStatus.valueOf(e.getStatusCode());
        return ApiResponseDTO.error(
                e.getMessage(),
                status.value(),
                status
        );
    }
}

package com.martingago.words.exceptions;

import com.martingago.words.dto.global.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;


@RestController
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFoundEntity(EntityNotFoundException e){
        return ApiResponse.error(e.getMessage(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateKey(DuplicateKeyException e){
        return ApiResponse.error(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException e){
        return  ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = BadRequestException.class)
    public  ResponseEntity<ApiResponse<Object>> handleBadRequestException(BadRequestException e){
        return ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public  ResponseEntity<ApiResponse<Object>> handleException(Exception e){
        return  ApiResponse.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Excepciones del microservicio de scrapping
    @ExceptionHandler(ScrapingServiceException.class)
    public ResponseEntity<ApiResponse<Object>> handleScrapingError(ScrapingServiceException e) {
        HttpStatus status = HttpStatus.valueOf(e.getStatusCode());
        return ApiResponse.error(
                e.getMessage(),
                status.value(),
                status
        );
    }
}

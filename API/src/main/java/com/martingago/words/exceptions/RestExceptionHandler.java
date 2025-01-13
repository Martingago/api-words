package com.martingago.words.exceptions;

import com.martingago.words.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
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
}

package com.martingago.words.exceptions.microservice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class WordGeneratedRelatedException extends RuntimeException{

    private final Object errorObject;

    /**
     * Clase de error personalizada que recibe el mensaje de error, y el objeto que ha causado dicho error.
     */
    public WordGeneratedRelatedException(String message, Object errorObject) {
        super(message);
        this.errorObject = errorObject;
    }
}

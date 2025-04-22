package com.martingago.words.exceptions.microservice;

public class ScrapingServiceException extends RuntimeException {
    private final int statusCode;

    public ScrapingServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

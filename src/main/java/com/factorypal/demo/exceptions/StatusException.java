package com.factorypal.demo.exceptions;

import org.springframework.http.HttpStatus;

public class StatusException extends RuntimeException {

    private final HttpStatus httpStatus;

    public StatusException(String message, HttpStatus status) {
        super(message);
        this.httpStatus = status;
    }

    public StatusException(HttpStatus status) {
        super();
        this.httpStatus = status;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
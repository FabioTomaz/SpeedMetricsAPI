package com.factorypal.demo.exceptions;

import org.springframework.http.HttpStatus;

public class IDNotRegisteredException extends StatusException {
    public IDNotRegisteredException(Long id) {
        super(String.format("Id %d not found in the internal repository.", id), HttpStatus.NOT_FOUND);
    }
}
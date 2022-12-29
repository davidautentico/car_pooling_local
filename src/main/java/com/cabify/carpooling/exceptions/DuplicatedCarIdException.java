package com.cabify.carpooling.exceptions;

public class DuplicatedCarIdException extends RuntimeException {
    public DuplicatedCarIdException(String message) {
        super(message);
    }
}

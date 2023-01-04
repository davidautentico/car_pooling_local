package com.drosa.cabify.carpooling.domain.exceptions;

public class DuplicatedCarIdException extends RuntimeException {
    public DuplicatedCarIdException(String message) {
        super(message);
    }
}

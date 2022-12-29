package com.cabify.carpooling.exceptions;

public class DuplicatedJourneyIdException extends RuntimeException {
    public DuplicatedJourneyIdException(String message) {
        super(message);
    }
}

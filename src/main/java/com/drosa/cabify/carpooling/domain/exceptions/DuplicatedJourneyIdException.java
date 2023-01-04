package com.drosa.cabify.carpooling.domain.exceptions;

public class DuplicatedJourneyIdException extends RuntimeException {
    public DuplicatedJourneyIdException(String message) {
        super(message);
    }
}

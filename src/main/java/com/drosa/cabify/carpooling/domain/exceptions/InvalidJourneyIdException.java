package com.drosa.cabify.carpooling.domain.exceptions;

public class InvalidJourneyIdException extends RuntimeException {
  public InvalidJourneyIdException(String message) {
    super(message);
  }
}

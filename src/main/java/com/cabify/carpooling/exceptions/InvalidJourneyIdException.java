package com.cabify.carpooling.exceptions;

public class InvalidJourneyIdException extends RuntimeException {
  public InvalidJourneyIdException(String message) {
    super(message);
  }
}

package com.drosa.cabify.carpooling.domain.exceptions;

public class JourneyNotFoundException extends RuntimeException {
  public JourneyNotFoundException(String message) {
    super(message);
  }
}

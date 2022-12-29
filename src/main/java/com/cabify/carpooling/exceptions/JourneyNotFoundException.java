package com.cabify.carpooling.exceptions;

public class JourneyNotFoundException extends RuntimeException {
  public JourneyNotFoundException(String message) {
    super(message);
  }
}

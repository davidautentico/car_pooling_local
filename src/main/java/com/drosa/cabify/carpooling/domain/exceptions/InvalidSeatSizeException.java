package com.drosa.cabify.carpooling.domain.exceptions;

public class InvalidSeatSizeException extends RuntimeException {
  public InvalidSeatSizeException(String message) {
    super(message);
  }
}

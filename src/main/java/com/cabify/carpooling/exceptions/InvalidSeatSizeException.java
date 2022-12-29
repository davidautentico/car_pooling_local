package com.cabify.carpooling.exceptions;

public class InvalidSeatSizeException extends RuntimeException {
  public InvalidSeatSizeException(String message) {
    super(message);
  }
}

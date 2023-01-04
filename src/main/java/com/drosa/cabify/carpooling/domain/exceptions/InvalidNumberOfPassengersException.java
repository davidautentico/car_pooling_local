package com.drosa.cabify.carpooling.domain.exceptions;

public class InvalidNumberOfPassengersException extends RuntimeException {

  public InvalidNumberOfPassengersException(String message) {
    super(message);
  }
}

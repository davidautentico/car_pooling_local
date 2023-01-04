package com.drosa.cabify.carpooling.domain.exceptions;

public class CarNotAssignedException extends RuntimeException {
  public CarNotAssignedException(String message) {
    super(message);
  }
}

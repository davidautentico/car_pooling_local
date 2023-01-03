package com.cabify.carpooling.exceptions;

public class CarNotAssignedException extends RuntimeException {
  public CarNotAssignedException(String message) {
    super(message);
  }
}

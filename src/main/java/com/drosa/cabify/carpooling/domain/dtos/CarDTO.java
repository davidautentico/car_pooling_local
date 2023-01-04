package com.drosa.cabify.carpooling.domain.dtos;

public class CarDTO {

  private final int id;
  private final int seats;

  public CarDTO(int id, int seats) {
    this.id = id;
    this.seats = seats;
  }

  public int getId() {
    return id;
  }

  public int getSeats() {
    return seats;
  }

  @Override
  public String toString() {
    return "Car: id = " + id + "," +
        " seats = "+ seats;
  }
}

package com.cabify.carpooling.dtos;

public class CarDTO {

  private int id;
  private int seats;

  public CarDTO(int id, int seats) {
    this.id = id;
    this.seats = seats;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getSeats() {
    return seats;
  }

  public void setSeats(int seats) {
    this.seats = seats;
  }
}

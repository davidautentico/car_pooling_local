package com.cabify.carpooling.dtos;

public class GroupDTO {

  private final int id;

  private final int people;

  public GroupDTO(int id, int people) {
    this.id = id;
    this.people = people;
  }

  public int getId() {
    return id;
  }

  public int getPassengers() {
    return people;
  }

  @Override
  public String toString() {
    return "Journey: id = " + id + "," +
        " people = "+ people + ",";
  }
}

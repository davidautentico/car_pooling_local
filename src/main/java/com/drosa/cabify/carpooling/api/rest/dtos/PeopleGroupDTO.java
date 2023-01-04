package com.drosa.cabify.carpooling.api.rest.dtos;

public class PeopleGroupDTO {
  private final int id;
  private final int people;

  public PeopleGroupDTO(int id, int people) {
    this.id = id;
    this.people = people;
  }

  public int getId() {
    return id;
  }

  public int getPeople() {
    return people;
  }

  @Override
  public String toString() {
    return "People group: id = " + id + "," +
        " people = "+ people + ",";
  }
}

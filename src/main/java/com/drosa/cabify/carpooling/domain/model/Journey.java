package com.drosa.cabify.carpooling.domain.model;

public class Journey {
    private final int id;
    private final int passengers;
    private Car assignedTo;
   
    public Journey(final int id, final int people) {
        this.id = id;
        this.passengers = people;
        this.assignedTo = null;
    }

    public int getId() {
        return id;
    }

    public int getPassengers() {
        return passengers;
    }

    public Car getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Car assignedTo) {
        this.assignedTo = assignedTo;
    }

    @Override
    public String toString() {
        return "Journey: id = " + id + "," +
            " passengers = "+ passengers + "," +
            " car = " + assignedTo;
    }
}

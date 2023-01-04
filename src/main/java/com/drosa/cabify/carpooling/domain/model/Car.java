package com.drosa.cabify.carpooling.domain.model;

public class Car {
    private final int id;
    private final int maxSeats;
    private int availableSeats;    

    public Car(final int id, final int seats) {
        this.id = id;
        this.maxSeats = seats;
        this.availableSeats = seats;
    }

    public int getID() {
        return id;
    }
    
    public int getMaxSeats() {
        return maxSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    @Override
    public String toString() {
        return "Car: id = " + id + "," +
            " maxSeats = "+ maxSeats + "," +
            " availableSeats = "+ availableSeats;
    }
}

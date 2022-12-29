package com.cabify.carpooling.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.cabify.carpooling.model.Car;
import org.springframework.stereotype.Repository;

@Repository
public class CarRepository {

  private final List<Car> cars;

  public CarRepository() {
    this.cars = Collections.synchronizedList(new ArrayList<>());
  }

  public void resetRepository(List<Car> cars) {
    this.cars.clear();
    this.cars.addAll(cars);
  }

  public Optional<Car> findAvailableCar(int neededSeats) {
    return cars
        .stream()
        .filter(c -> neededSeats <= c.getAvailableSeats())
        .findFirst();
  }

  public int count(){
    return cars.size();
  }
}

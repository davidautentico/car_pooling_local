package com.drosa.cabify.carpooling.infrastructure.dataStores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.drosa.cabify.carpooling.domain.model.Car;
import com.drosa.cabify.carpooling.domain.repositories.CarRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CarRepositoryImpl implements CarRepository {

  private final List<Car> cars;

  public CarRepositoryImpl() {
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
}

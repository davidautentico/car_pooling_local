package com.drosa.cabify.carpooling.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.drosa.cabify.carpooling.domain.model.Car;

public interface CarRepository {

  void resetRepository(List<Car> cars);

  Optional<Car> findAvailableCar(int neededSeats);
}

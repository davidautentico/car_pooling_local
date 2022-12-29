package com.cabify.carpooling.helpers;

import java.util.ArrayList;
import java.util.List;

import com.cabify.carpooling.exceptions.DuplicatedCarIdException;
import com.cabify.carpooling.exceptions.InvalidSeatSizeException;
import com.cabify.carpooling.model.Car;
import org.springframework.stereotype.Component;

@Component
public class CarListFactory {

  //TODO: MEJORAR CON HASHMAP
  public List<Car> createValidCarList(List<Car> cars) {
    final List<Car> cleanCarList = new ArrayList<>();

    for (Car c1 : cars) {
      for (Car c2 : cleanCarList ) {
        if (c1.getID() == c2.getID()) {
          throw new DuplicatedCarIdException(String.format("Found car with ID duplicated <%s>", c1.getID()));
        }
        if (c1.getMaxSeats() < 4 || c1.getMaxSeats() > 6) {
          throw new InvalidSeatSizeException(
              String.format("Found car with ID <%s> and invalid seat size <%d>", c1.getID(), c1.getMaxSeats()));
        }
      }
      cleanCarList.add(c1);
    }

    return cleanCarList;
  }
}

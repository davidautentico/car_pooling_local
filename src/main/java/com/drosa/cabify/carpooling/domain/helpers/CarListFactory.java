package com.drosa.cabify.carpooling.domain.helpers;

import java.util.ArrayList;
import java.util.List;

import com.drosa.cabify.carpooling.domain.exceptions.DuplicatedCarIdException;
import com.drosa.cabify.carpooling.domain.exceptions.InvalidSeatSizeException;
import com.drosa.cabify.carpooling.domain.model.Car;
import com.drosa.cabify.carpooling.domain.repositories.ConfigurationRepository;
import org.springframework.stereotype.Component;

@Component
public class CarListFactory {

  private final ConfigurationRepository configurationRepository;

  public CarListFactory(ConfigurationRepository configurationRepository) {
    this.configurationRepository = configurationRepository;
  }

  public List<Car> createValidCarList(List<Car> cars) {
    final List<Car> cleanCarList = new ArrayList<>();

    for (Car c1 : cars) {
      for (Car c2 : cleanCarList) {
        if (c1.getID() == c2.getID()) {
          throw new DuplicatedCarIdException(String.format("Found car with ID duplicated <%s>", c1.getID()));
        }
        if (c1.getMaxSeats() < configurationRepository.getMinCarSeats() || c1.getMaxSeats() > configurationRepository.getMaxCarSeats()) {
          throw new InvalidSeatSizeException(
              String.format("Found car with ID <%s> and invalid seat size <%d>", c1.getID(), c1.getMaxSeats()));
        }
      }
      cleanCarList.add(c1);
    }

    return cleanCarList;
  }
}

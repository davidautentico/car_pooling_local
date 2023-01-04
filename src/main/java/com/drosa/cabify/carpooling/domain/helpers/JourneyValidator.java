package com.drosa.cabify.carpooling.domain.helpers;

import com.drosa.cabify.carpooling.domain.exceptions.InvalidJourneyIdException;
import com.drosa.cabify.carpooling.domain.exceptions.InvalidNumberOfPassengersException;
import com.drosa.cabify.carpooling.domain.model.Journey;
import com.drosa.cabify.carpooling.domain.repositories.ConfigurationRepository;
import org.springframework.stereotype.Component;

@Component
public class JourneyValidator {

  private final ConfigurationRepository configurationRepository;

  public JourneyValidator(ConfigurationRepository configurationRepository) {
    this.configurationRepository = configurationRepository;
  }

  public void validateJourney(Journey journey) {
    validateJourneyId(journey.getId());

    if (journey.getPassengers() < configurationRepository.getMinPassengers()
        || journey.getPassengers() > configurationRepository.getMaxPassengers()) {
      throw new InvalidNumberOfPassengersException(
          String.format("Journey ID <%d> has a invalid number of passengers <%d>", journey.getId(), journey.getPassengers()));
    }
  }

  public void validateJourneyId(int journeyId) {
    if (journeyId <= 0) {
      throw new InvalidJourneyIdException(String.format("Journey ID <%d> is invalid", journeyId));
    }
  }
}

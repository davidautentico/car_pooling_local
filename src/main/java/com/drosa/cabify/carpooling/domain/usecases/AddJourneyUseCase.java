package com.drosa.cabify.carpooling.domain.usecases;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.drosa.cabify.carpooling.domain.exceptions.DuplicatedJourneyIdException;
import com.drosa.cabify.carpooling.domain.helpers.JourneyValidator;
import com.drosa.cabify.carpooling.domain.model.Journey;
import com.drosa.cabify.carpooling.domain.repositories.CarRepository;
import com.drosa.cabify.carpooling.domain.repositories.JourneyRepository;
import com.drosa.cabify.carpooling.domain.repositories.PendingJourneyRepository;
import com.drosa.cabify.carpooling.domain.usecases.stereotypes.UseCase;

@UseCase
public class AddJourneyUseCase {

  private final CarRepository carRepository;

  private final JourneyRepository journeyRepository;

  private final PendingJourneyRepository pendingJourneyRepository;

  private final ReentrantReadWriteLock reentrantReadWriteLock;

  private final JourneyValidator journeyValidator;

  public AddJourneyUseCase(CarRepository carRepository, JourneyRepository journeyRepository,
      PendingJourneyRepository pendingJourneyRepository, ReentrantReadWriteLock reentrantReadWriteLock, JourneyValidator journeyValidator) {
    this.carRepository = carRepository;
    this.journeyRepository = journeyRepository;
    this.pendingJourneyRepository = pendingJourneyRepository;
    this.reentrantReadWriteLock = reentrantReadWriteLock;
    this.journeyValidator = journeyValidator;
  }

  public void dispatch(final Journey journey) {

    journeyValidator.validateJourney(journey);

    reentrantReadWriteLock.writeLock().lock();
    try {
      var other = journeyRepository.find(journey);
      if (other.isPresent()) {
        throw new DuplicatedJourneyIdException(String.format("Journey ID <%d> is already in use", other.get().getId()));
      }

      addJourney(journey);
    } finally {
      reentrantReadWriteLock.writeLock().unlock();
    }
  }

  private void addJourney(final Journey journey) {

    var selectedCar = carRepository.findAvailableCar(journey.getPassengers());
    if (selectedCar.isPresent()) {
      var car = selectedCar.get();
      journey.setAssignedTo(car);
      car.setAvailableSeats(car.getAvailableSeats() - journey.getPassengers());
    } else {
      pendingJourneyRepository.addPendingJourney(journey);
    }

    journeyRepository.add(journey);
  }
}

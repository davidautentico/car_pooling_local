package com.drosa.cabify.carpooling.domain.usecases;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.drosa.cabify.carpooling.domain.exceptions.CarNotAssignedException;
import com.drosa.cabify.carpooling.domain.exceptions.JourneyNotFoundException;
import com.drosa.cabify.carpooling.domain.helpers.JourneyValidator;
import com.drosa.cabify.carpooling.domain.model.Car;
import com.drosa.cabify.carpooling.domain.model.Journey;
import com.drosa.cabify.carpooling.domain.repositories.CarRepository;
import com.drosa.cabify.carpooling.domain.repositories.JourneyRepository;
import com.drosa.cabify.carpooling.domain.repositories.PendingJourneyRepository;
import com.drosa.cabify.carpooling.domain.usecases.stereotypes.UseCase;

@UseCase
public class DropOffJourneyUseCase {

  private final JourneyRepository journeyRepository;

  private final PendingJourneyRepository pendingJourneyRepository;

  private final ReentrantReadWriteLock reentrantReadWriteLock;

  private final JourneyValidator journeyValidator;

  public DropOffJourneyUseCase(JourneyRepository journeyRepository,
      PendingJourneyRepository pendingJourneyRepository, ReentrantReadWriteLock reentrantReadWriteLock, JourneyValidator journeyValidator) {
    this.journeyRepository = journeyRepository;
    this.pendingJourneyRepository = pendingJourneyRepository;
    this.reentrantReadWriteLock = reentrantReadWriteLock;
    this.journeyValidator = journeyValidator;
  }

  public void dispatch(final int journeyID) {

    journeyValidator.validateJourneyId(journeyID);

    reentrantReadWriteLock.writeLock().lock();
    try {
      var journey = journeyRepository.find(journeyID);
      if (journey.isEmpty()) {
        throw new JourneyNotFoundException(String.format("Journey ID <%d> not found", journeyID));
      }

      removeJourney(journey.get());
    } finally {
      reentrantReadWriteLock.writeLock().unlock();
    }
  }

  private void removeJourney(final Journey journey) {

    var car = journey.getAssignedTo();
    if (car != null) {
      car.setAvailableSeats(car.getAvailableSeats() + journey.getPassengers());
      carReassign(car);
    } else {
      pendingJourneyRepository.removeIfExists(journey);
      throw new CarNotAssignedException(String.format("Journey ID <%d> has not car assigned", journey.getId()));
    }

    journeyRepository.remove(journey.getId());
  }

  private void carReassign(final Car car) {

    var journey = pendingJourneyRepository.findFirstJourneyWithEqualOrLessPassengers(car.getAvailableSeats());

    journey.ifPresent(presentJourney -> {
      presentJourney.setAssignedTo(car);
      car.setAvailableSeats(car.getAvailableSeats() - presentJourney.getPassengers());
      pendingJourneyRepository.removeIfExists(presentJourney);
    });
  }
}

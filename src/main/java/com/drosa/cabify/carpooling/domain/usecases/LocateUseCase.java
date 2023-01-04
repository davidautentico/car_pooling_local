package com.drosa.cabify.carpooling.domain.usecases;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.drosa.cabify.carpooling.domain.exceptions.CarNotAssignedException;
import com.drosa.cabify.carpooling.domain.exceptions.JourneyNotFoundException;
import com.drosa.cabify.carpooling.domain.helpers.JourneyValidator;
import com.drosa.cabify.carpooling.domain.model.Car;
import com.drosa.cabify.carpooling.domain.repositories.JourneyRepository;
import com.drosa.cabify.carpooling.domain.usecases.stereotypes.UseCase;

@UseCase
public class LocateUseCase {

  private final JourneyRepository journeyRepository;

  private final ReentrantReadWriteLock reentrantReadWriteLock;

  private final JourneyValidator journeyValidator;

  public LocateUseCase(JourneyRepository journeyRepository, ReentrantReadWriteLock reentrantReadWriteLock,
      JourneyValidator journeyValidator) {
    this.journeyRepository = journeyRepository;
    this.reentrantReadWriteLock = reentrantReadWriteLock;
    this.journeyValidator = journeyValidator;
  }

  public Car dispatch(final int journeyID) {

    journeyValidator.validateJourneyId(journeyID);

    reentrantReadWriteLock.readLock().lock();
    try {
      var journey = journeyRepository.find(journeyID);
      if (journey.isEmpty()) {
        throw new JourneyNotFoundException(String.format("Journey ID <%d> not found", journeyID));
      }

      var carAssignedTo = journey.get().getAssignedTo();
      if (carAssignedTo == null) {
        throw new CarNotAssignedException(String.format("Journey ID <%d> has not car assigned to", journeyID));
      }

      return carAssignedTo;
    } finally {
      reentrantReadWriteLock.readLock().unlock();
    }
  }
}

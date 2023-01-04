package com.drosa.cabify.carpooling.domain.usecases;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.drosa.cabify.carpooling.domain.helpers.CarListFactory;
import com.drosa.cabify.carpooling.domain.model.Car;
import com.drosa.cabify.carpooling.domain.repositories.CarRepository;
import com.drosa.cabify.carpooling.domain.repositories.JourneyRepository;
import com.drosa.cabify.carpooling.domain.repositories.PendingJourneyRepository;
import com.drosa.cabify.carpooling.domain.usecases.stereotypes.UseCase;

@UseCase
public class ResetCarsAndJourneysUseCase {

  private final CarRepository carRepository;

  private final JourneyRepository journeyRepository;

  private final PendingJourneyRepository pendingJourneyRepository;

  private final ReentrantReadWriteLock reentrantReadWriteLock;

  private final CarListFactory carListFactory;

  public ResetCarsAndJourneysUseCase(CarRepository carRepository, JourneyRepository journeyRepository,
      PendingJourneyRepository pendingJourneyRepository, ReentrantReadWriteLock reentrantReadWriteLock, CarListFactory carListFactory) {
    this.carRepository = carRepository;
    this.journeyRepository = journeyRepository;
    this.pendingJourneyRepository = pendingJourneyRepository;
    this.reentrantReadWriteLock = reentrantReadWriteLock;
    this.carListFactory = carListFactory;
  }

  public void dispatch(final List<Car> carList) {

    reentrantReadWriteLock.writeLock().lock();
    try {
      carRepository.resetRepository(carListFactory.createValidCarList(carList));
      journeyRepository.resetJourneys(Collections.emptyList());
      pendingJourneyRepository.reset();
    } finally {
      reentrantReadWriteLock.writeLock().unlock();
    }
  }
}

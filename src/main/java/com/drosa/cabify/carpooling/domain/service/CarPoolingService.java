package com.drosa.cabify.carpooling.domain.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.drosa.cabify.carpooling.domain.exceptions.CarNotAssignedException;
import com.drosa.cabify.carpooling.domain.exceptions.DuplicatedJourneyIdException;
import com.drosa.cabify.carpooling.domain.exceptions.JourneyNotFoundException;
import com.drosa.cabify.carpooling.domain.helpers.CarListFactory;
import com.drosa.cabify.carpooling.domain.helpers.JourneyValidator;
import com.drosa.cabify.carpooling.domain.repositories.CarRepository;
import com.drosa.cabify.carpooling.domain.repositories.ConfigurationRepository;
import com.drosa.cabify.carpooling.domain.repositories.JourneyRepository;
import org.springframework.stereotype.Service;
import com.drosa.cabify.carpooling.domain.model.Car;
import com.drosa.cabify.carpooling.domain.model.Journey;

@Service
public class CarPoolingService {

  private final CarRepository carRepository;

  private final JourneyRepository journeyRepository;

  private final List<Journey> pending;

  private final Lock readLock;

  private final Lock writeLock;

  private final CarListFactory carListFactory;

  private final JourneyValidator journeyValidator;

  private final ConfigurationRepository configurationRepository;

  public CarPoolingService(final CarRepository carRepository, final JourneyRepository journeyRepository,
      final CarListFactory carListFactory,
      final JourneyValidator journeyValidator, final ConfigurationRepository configurationRepository) {

    this.journeyValidator = journeyValidator;
    this.configurationRepository = configurationRepository;
    final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    this.readLock = rwLock.readLock();
    this.writeLock = rwLock.writeLock();
    this.carRepository = carRepository;
    this.journeyRepository = journeyRepository;
    this.carListFactory = carListFactory;
    this.pending = new ArrayList<>();
  }

  public void resetCars(final List<Car> carList) {
    writeLock.lock();
    try {
      carRepository.resetRepository(carListFactory.createValidCarList(carList));
      journeyRepository.resetJourneys(Collections.emptyList());
      this.pending.clear();
    } finally {
      writeLock.unlock();
    }
  }

  public void newJourney(final Journey journey) {

    journeyValidator.validateJourney(journey);

    this.writeLock.lock();
    try {
      Optional<Journey> other = journeyRepository.find(journey);
      if (other.isPresent()) {
        throw new DuplicatedJourneyIdException(String.format("Journey ID <%d> is already in use", other.get().getId()));
      }
      Optional<Car> selectedCar = carRepository.findAvailableCar(journey.getPassengers());
      if (selectedCar.isPresent()) {
        Car car = selectedCar.get();
        journey.setAssignedTo(car);
        car.setAvailableSeats(car.getAvailableSeats() - journey.getPassengers());
      } else {
        this.pending.add(journey);
      }
      journeyRepository.add(journey);

    } finally {
      writeLock.unlock();
    }
  }

  public void dropoff(final int journeyID) {

    journeyValidator.validateJourneyId(journeyID);

    writeLock.lock();
    try {
      Optional<Journey> journey = journeyRepository.find(journeyID);
      if (journey.isEmpty()) {
        throw new JourneyNotFoundException(String.format("Journey ID <%d> not found", journeyID));
      }

      Car car = journey.get().getAssignedTo();
      journeyRepository.remove(journey.get().getId());
      if (car != null) {
        car.setAvailableSeats(car.getAvailableSeats() + journey.get().getPassengers());
        reassign(car);
      } else {
        pending.removeIf(j -> j.getId() == journey.get().getId());
        throw new CarNotAssignedException(String.format("Journey ID <%d> has no car assigned", journeyID));
      }
    } finally {
      writeLock.unlock();
    }
  }

  public Car locate(final int journeyID) {

    journeyValidator.validateJourneyId(journeyID);

    this.readLock.lock();
    try {
      Optional<Journey> journey = journeyRepository.find(journeyID);
      if (journey.isEmpty()) {
        throw new JourneyNotFoundException(String.format("Journey ID <%d> not found", journeyID));
      }
      return journey.get().getAssignedTo();
    } finally {
      readLock.unlock();
    }
  }

  private void reassign(final Car car) {
    Optional<Journey> journey = this.pending
        .stream()
        .filter(j -> j.getPassengers() <= car.getAvailableSeats())
        .findFirst();

    journey.ifPresent(j -> {
      j.setAssignedTo(car);
      car.setAvailableSeats(car.getAvailableSeats() - j.getPassengers());
      this.pending.removeIf(j2 -> j2.getId() == j.getId());
    });
  }
}

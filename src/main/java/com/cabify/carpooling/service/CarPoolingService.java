package com.cabify.carpooling.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.cabify.carpooling.exceptions.CarNotAssignedException;
import com.cabify.carpooling.exceptions.DuplicatedJourneyIdException;
import com.cabify.carpooling.exceptions.InvalidJourneyIdException;
import com.cabify.carpooling.exceptions.JourneyNotFoundException;
import com.cabify.carpooling.helpers.CarListFactory;
import com.cabify.carpooling.repositories.CarRepository;
import com.cabify.carpooling.repositories.JourneyRepository;
import org.springframework.stereotype.Service;
import com.cabify.carpooling.model.Car;
import com.cabify.carpooling.model.Journey;

@Service
public class CarPoolingService {

  private final CarRepository carRepository;

  private final JourneyRepository journeyRepository;

  private final List<Journey> pending;

  private final Lock readLock;

  private final Lock writeLock;

  private final CarListFactory carListFactory;

  public CarPoolingService(CarRepository carRepository, JourneyRepository journeyRepository, final CarListFactory carListFactory) {
    final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    this.readLock = rwLock.readLock();
    this.writeLock = rwLock.writeLock();
    this.carRepository = carRepository;
    this.journeyRepository = journeyRepository;
    this.carListFactory = carListFactory;
    this.pending = Collections.synchronizedList(new ArrayList<>());
  }

  public void resetCars(List<Car> carList) {
    System.out.println("[resetCars] write lock");
    writeLock.lock();

    try {
      System.out.println("[resetCars] creating collections...");
      carRepository.resetRepository(carListFactory.createValidCarList(carList));
      journeyRepository.resetJourneys(Collections.emptyList());
      this.pending.clear();
      System.out.println("[resetCars] collections created...");
    } finally {
      System.out.println("[resetCars] write unlock");
      writeLock.unlock();
    }
  }

  public void newJourney(int groupId, int passengers) {

    if (groupId <= 0) {
      throw new InvalidJourneyIdException(String.format("Journey ID <%d> is invalid", groupId));
    }
    System.out.println("[new journey] write lock");
    this.writeLock.lock();
    try {
      Journey journey = new Journey(groupId, passengers);
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
      System.out.println("[new journey] write unlock");
      writeLock.unlock();
    }
  }

  public void dropoff(int journeyID) {

    if (journeyID <= 0) {
      throw new InvalidJourneyIdException(String.format("Journey ID <%d> is invalid", journeyID));
    }

    System.out.println("[dropoff] write lock");
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
      System.out.println("[dropoff] write unlock");
      writeLock.unlock();
    }
  }

  /**
   * //problema de concurrencia, antes de reasignar pueden haber sido borrados..
   * @param car
   */
  private void reassign(Car car) {
    Optional<Journey> journey = this.pending
        .stream()
        .filter(j -> j.getPassengers() <= car.getAvailableSeats())
        .findFirst();

    System.out.println("Pending and cars size: " + pending.size() + " " + carRepository.count());

    journey.ifPresent(j -> {
      System.out.format(">> Car %d reassigned to journey %d\n", car.getID(), j.getId());
      j.setAssignedTo(car);
      car.setAvailableSeats(car.getAvailableSeats() - j.getPassengers());
      this.pending.removeIf(j2 -> j2.getId() == j.getId());
    });
  }

  /**
   * Problema de concurrencia journeys podría ser borrado mientras se itera en el find, al no ser atómico
   */
  public Car locate(int journeyID) {
    System.out.println("[locate] read lock - journeys size: " + journeyRepository.count());
    this.readLock.lock();
    try {
      Optional<Journey> journey = journeyRepository.find(journeyID);
      if (journey.isEmpty()) {
        System.out.println("[locate] journey not found <%d> "+journeyID);
        throw new JourneyNotFoundException(String.format("Journey ID <%d> not found", journeyID));
      }
      return journey.get().getAssignedTo();
    } finally {
      System.out.println("[locate] read unlock");
      readLock.unlock();
    }
  }
}

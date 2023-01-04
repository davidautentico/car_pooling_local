package com.drosa.cabify.carpooling.domain.repositories;

public interface ConfigurationRepository {

  int getMaxCarSeats();

  int getMinCarSeats();

  int getMinPassengers();

  int getMaxPassengers();
}

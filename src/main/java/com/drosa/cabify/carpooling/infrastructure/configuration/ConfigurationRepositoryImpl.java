package com.drosa.cabify.carpooling.infrastructure.configuration;

import com.drosa.cabify.carpooling.domain.repositories.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationRepositoryImpl implements ConfigurationRepository {

  @Value("${com.cabify.pooling.maxCarSeats:6}")
  private int maxCarSeats;

  @Value("${com.cabify.pooling.minCarSeats:4}")
  private int minCarSeats;

  @Value("${com.cabify.pooling.maxPassengers:6}")
  private int maxPassengers;

  @Value("${com.cabify.pooling.minPassengers:1}")
  private int minPassengers;

  @Override
  public int getMaxCarSeats() {
    return maxCarSeats;
  }

  @Override
  public int getMinCarSeats() {
    return minCarSeats;
  }

  @Override
  public int getMinPassengers() {
    return minPassengers;
  }

  @Override
  public int getMaxPassengers() {
    return maxPassengers;
  }
}

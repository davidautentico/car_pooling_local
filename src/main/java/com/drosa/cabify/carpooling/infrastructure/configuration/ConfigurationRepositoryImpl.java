package com.drosa.cabify.carpooling.infrastructure.configuration;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.drosa.cabify.carpooling.domain.repositories.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationRepositoryImpl implements ConfigurationRepository {

  @Value("${com.drosa.cabify.pooling.maxCarSeats:6}")
  private int maxCarSeats;

  @Value("${com.drosa.cabify.pooling.minCarSeats:4}")
  private int minCarSeats;

  @Value("${com.drosa.cabify.pooling.maxPassengers:6}")
  private int maxPassengers;

  @Value("${com.drosa.cabify.pooling.minPassengers:1}")
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

  @Bean
  public ReentrantReadWriteLock reentrantReadWriteLockBean(){
    return new ReentrantReadWriteLock();
  }
}

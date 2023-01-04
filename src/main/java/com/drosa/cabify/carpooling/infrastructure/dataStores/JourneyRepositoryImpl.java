package com.drosa.cabify.carpooling.infrastructure.dataStores;


import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.drosa.cabify.carpooling.domain.model.Journey;
import com.drosa.cabify.carpooling.domain.repositories.JourneyRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JourneyRepositoryImpl implements JourneyRepository {

  private final ConcurrentHashMap<Integer, Journey> journeys;

  public JourneyRepositoryImpl() {
    this.journeys = new ConcurrentHashMap<>();
  }

  public void resetJourneys(final List<Journey> journeys) {
    this.journeys.clear();
    this.journeys.putAll(journeys.stream()
        .collect(Collectors.toMap(Journey::getId, Function.identity()))
    );
  }

  public Optional<Journey> find(final Journey journey) {
    return findJourneyById(journey.getId());
  }

  public Optional<Journey> find(final int journeyId) {
    return findJourneyById(journeyId);
  }

  public void remove(int journeyId) {
    this.journeys.remove(journeyId);
  }

  public void add(Journey journey) {
    journeys.put(journey.getId(), journey);
  }

  private Optional<Journey> findJourneyById(final int journeyId) {
    return Optional.ofNullable(this.journeys.get(journeyId));
  }

}

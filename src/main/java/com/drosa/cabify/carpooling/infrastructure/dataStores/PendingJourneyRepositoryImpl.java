package com.drosa.cabify.carpooling.infrastructure.dataStores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.drosa.cabify.carpooling.domain.model.Journey;
import com.drosa.cabify.carpooling.domain.repositories.PendingJourneyRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PendingJourneyRepositoryImpl implements PendingJourneyRepository {

  private final List<Journey> journeys;

  public PendingJourneyRepositoryImpl() {
    this.journeys = Collections.synchronizedList(new ArrayList<>());
  }

  @Override
  public void reset() {
    journeys.clear();
  }

  @Override
  public void removeIfExists(final Journey journey) {
    journeys.removeIf(journeyToRemove -> journeyToRemove.getId() == journey.getId());
  }

  @Override
  public void addPendingJourney(final Journey journey) {
    journeys.add(journey);
  }

  @Override
  public Optional<Journey> findFirstJourneyWithEqualOrLessPassengers(int maxPassengers) {
    return Optional.empty();
  }
}

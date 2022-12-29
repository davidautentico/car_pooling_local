package com.cabify.carpooling.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.cabify.carpooling.model.Journey;
import org.springframework.stereotype.Repository;

@Repository
public class JourneyRepository {

  private final List<Journey> journeys;

  public JourneyRepository() {
    this.journeys = Collections.synchronizedList(new ArrayList<>());
  }

  public void resetJourneys(final List<Journey> journeys) {
    this.journeys.clear();
    this.journeys.addAll(journeys);
  }

  public Optional<Journey> find(final Journey journey) {
    return findJourneyById(journey.getId());
  }

  public Optional<Journey> find(final int journeyId) {
    return findJourneyById(journeyId);
  }

  public void remove(int journeyId) {
    this.journeys.removeIf(j -> j.getId() == journeyId);
  }

  public void add(Journey journey) {
    journeys.add(journey);
  }

  public int count() {
    return journeys.size();
  }

  private Optional<Journey> findJourneyById(final int journeyId) {
    return this.journeys
        .stream()
        .filter(j -> j.getId() == journeyId)
        .findAny();
  }


}

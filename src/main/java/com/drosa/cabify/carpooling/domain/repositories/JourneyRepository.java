package com.drosa.cabify.carpooling.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.drosa.cabify.carpooling.domain.model.Journey;

public interface JourneyRepository {

  void resetJourneys(final List<Journey> journeys);

  Optional<Journey> find(final Journey journey);

  Optional<Journey> find(final int journeyId);

  void remove(int journeyId);

  void add(Journey journey);
}

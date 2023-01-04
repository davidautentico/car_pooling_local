package com.drosa.cabify.carpooling.domain.repositories;

import java.util.Optional;

import com.drosa.cabify.carpooling.domain.model.Journey;

public interface PendingJourneyRepository {

  void reset();

  void removeIfExists(final Journey journey);

  void addPendingJourney(final Journey journey);

  Optional<Journey> findFirstJourneyWithEqualOrLessPassengers(int maxPassengers);
}

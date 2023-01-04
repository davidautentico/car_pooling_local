package com.drosa.cabify.carpooling.api.rest.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.drosa.cabify.carpooling.api.rest.dtos.CarDTO;
import com.drosa.cabify.carpooling.api.rest.dtos.PeopleGroupDTO;
import com.drosa.cabify.carpooling.domain.model.Journey;
import com.drosa.cabify.carpooling.domain.usecases.AddJourneyUseCase;
import com.drosa.cabify.carpooling.domain.usecases.DropOffJourneyUseCase;
import com.drosa.cabify.carpooling.domain.usecases.LocateUseCase;
import com.drosa.cabify.carpooling.domain.usecases.ResetCarsAndJourneysUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.drosa.cabify.carpooling.domain.model.Car;

@RestController
public class CarPoolingController {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final LocateUseCase locateUseCase;

  private final ResetCarsAndJourneysUseCase resetCarsAndJourneysUseCase;

  private final AddJourneyUseCase addJourneyUseCase;

  private final DropOffJourneyUseCase dropOffJourneyUseCase;

  public CarPoolingController(LocateUseCase locateUseCase,
      ResetCarsAndJourneysUseCase resetCarsAndJourneysUseCase, AddJourneyUseCase addJourneyUseCase,
      DropOffJourneyUseCase dropOffJourneyUseCase) {
    this.locateUseCase = locateUseCase;
    this.resetCarsAndJourneysUseCase = resetCarsAndJourneysUseCase;
    this.addJourneyUseCase = addJourneyUseCase;
    this.dropOffJourneyUseCase = dropOffJourneyUseCase;
  }

  @GetMapping("/status")
  @ResponseStatus(HttpStatus.OK)
  public void getStatus() {
    log.info("[Status request received]");
  }

  @PutMapping(value = "/cars",
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseStatus(HttpStatus.OK)
  public void putCars(@RequestBody List<CarDTO> carDTOs) {

    log.info("[Cars request received] cars: " + carDTOs);

    var cars = carDTOs
        .stream()
        .map(carDto -> new Car(carDto.getId(), carDto.getSeats()))
        .collect(Collectors.toList());

    resetCarsAndJourneysUseCase.dispatch(cars);
  }

  @PostMapping(value = "/journey",
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseStatus(HttpStatus.OK)
  public void postJourney(@RequestBody PeopleGroupDTO peopleGroupDTO) {

    log.info("[Journey request received] people group: " + peopleGroupDTO);

    addJourneyUseCase.dispatch(new Journey(peopleGroupDTO.getId(), peopleGroupDTO.getPeople()));
  }

  @PostMapping(value = "/dropoff",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
  )
  @ResponseStatus(HttpStatus.OK)
  public void postDropoff(@RequestParam("ID") int journeyID) {

    log.info("[Dropoff request received] Journey: " + journeyID);

    dropOffJourneyUseCase.dispatch(journeyID);
  }

  @PostMapping(
      value = "/locate",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody ResponseEntity<CarDTO> postLocate(@RequestParam("ID") final int journeyID) {

    log.info("[Locate request received] journeyID: " + journeyID);

    var car = locateUseCase.dispatch(journeyID);

    return new ResponseEntity<>(new CarDTO(car.getID(), car.getMaxSeats()), HttpStatus.OK);
  }
}

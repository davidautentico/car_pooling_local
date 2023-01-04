package com.drosa.cabify.carpooling.api.rest.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.drosa.cabify.carpooling.domain.dtos.CarDTO;
import com.drosa.cabify.carpooling.domain.dtos.PeopleGroupDTO;
import com.drosa.cabify.carpooling.domain.model.Journey;
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
import com.drosa.cabify.carpooling.domain.service.CarPoolingService;

@RestController
public class CarPoolingController {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final CarPoolingService carJourneyService;

  public CarPoolingController(CarPoolingService carJourneyService) {
    this.carJourneyService = carJourneyService;
  }

  @GetMapping("/status")
  @ResponseStatus(HttpStatus.OK)
  public void getStatus() {
    log.info("Status request received");
  }

  @PutMapping(value = "/cars",
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseStatus(HttpStatus.OK)
  public void putCars(@RequestBody List<CarDTO> carDTOs) {

    log.info("*** Cars request received cars: " + carDTOs);

    List<Car> cars = carDTOs
        .stream()
        .map(carDto -> new Car(carDto.getId(), carDto.getSeats()))
        .collect(Collectors.toList());


    carJourneyService.resetCars(cars);
  }

  @PostMapping(value = "/journey",
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseStatus(HttpStatus.OK)
  public void postJourney(@RequestBody PeopleGroupDTO peopleGroupDTO) {

    log.info("[Journey request received] people group: " + peopleGroupDTO);

    carJourneyService.newJourney(new Journey(peopleGroupDTO.getId(), peopleGroupDTO.getPeople()));
  }

  @PostMapping(value = "/dropoff",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
  )
  @ResponseStatus(HttpStatus.OK)
  public void postDropoff(@RequestParam("ID") int journeyID) {

    log.info("[Dropoff request received] Journey: " + journeyID);

    carJourneyService.dropoff(journeyID);
  }

  @PostMapping(
      value = "/locate",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public @ResponseBody ResponseEntity<CarDTO> postLocate(@RequestParam("ID") final int journeyID) {

    log.info("[Locate request received] ID: " + journeyID);

    Car car = carJourneyService.locate(journeyID);
    if (car == null) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    return new ResponseEntity<>(new CarDTO(car.getID(), car.getMaxSeats()), HttpStatus.OK);
  }

}

package com.cabify.carpooling.controller;

import java.util.List;
import java.util.NoSuchElementException;

import com.cabify.carpooling.dtos.CarDTO;
import com.cabify.carpooling.dtos.GroupDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.cabify.carpooling.model.Car;
import com.cabify.carpooling.service.CarPoolingService;

@RestController
public class CarPoolingController {

  java.util.logging.Logger log = java.util.logging.Logger.getLogger(this.getClass().getName());

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private CarPoolingService carJourneyService;

  @GetMapping("/status")
  @ResponseStatus(HttpStatus.OK)
  public void getStatus() {
    log.info("Status request received");
  }

  @PutMapping(value = "/cars", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public void putCars(@RequestBody List<Car> cars) {

    log.info("*** Cars request received cars: " + cars);

    carJourneyService.resetCars(cars);

    log.info("*** Cars request done ");
  }

  @PostMapping("/journey")
  public ResponseEntity<Void> postJourney(@RequestBody GroupDTO group) {

    log.info("[*** Journey request received] Group: " + group);

    if (group.getId() <= 0) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    carJourneyService.newJourney(group.getId(), group.getPassengers());
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @PostMapping("/dropoff")
  public ResponseEntity<Void> postDropoff(@RequestParam("ID") int journeyID) {

    log.info("[*** Dropoff request received] Journey: " + journeyID);

    if (journeyID <= 0) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    try {
      Car car = carJourneyService.dropoff(journeyID);
      log.info("[*** Dropoff request received] waiting...");

      if (car != null) {
        return new ResponseEntity<>(HttpStatus.OK);
      }

      log.info("[*** Dropoff request received] end Journey: " + journeyID);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (NoSuchElementException e) {
      log.info("[*** Dropoff request received] NOT FOUND");
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping(
      value = "/locate",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = "application/json")
  public @ResponseBody ResponseEntity<CarDTO> postLocate(@RequestParam("ID") final int journeyID) {

    log.info("[*** Locate request received] ID: " + journeyID);

    if (journeyID <= 0) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    try {
      Car car = carJourneyService.locate(journeyID);
      if (car == null) {

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      CarDTO carDto = new CarDTO(car.getID(), car.getMaxSeats());
      log.info("*** Locate request car found: " + objectMapper.writeValueAsString(carDto));
      return new ResponseEntity<>(carDto, HttpStatus.OK);
    } catch (NoSuchElementException e) {
      log.info("*** Locate request car found: No such Element exception");
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch (JsonProcessingException e) {
      log.info("*** Locate request car found: JsonProcessingException");
      throw new RuntimeException(e);
    }
  }
}

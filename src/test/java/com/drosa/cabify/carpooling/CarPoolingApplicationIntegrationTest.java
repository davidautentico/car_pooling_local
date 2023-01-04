package com.drosa.cabify.carpooling;

import java.util.List;

import com.drosa.cabify.carpooling.api.rest.dtos.CarDTO;
import com.drosa.cabify.carpooling.api.rest.dtos.PeopleGroupDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class CarPoolingApplicationIntegrationTest {

  private static final int VALID_JOURNEY_ID = 1;

  private static final int INVALID_MAX_SEAT_SIZE = 7;

  private static final int INVALID_MIN_SEAT_SIZE = 3;

  private static final int INVALID_MAX_PASSENGERS = 25;

  private static final int INVALID_MIN_PASSENGERS = 0;

  private static final int INVALID_JOURNEY_ID = -1;

  private static final int VALID_JOURNEY_NOT_FOUND_ID = 100;

  private static final int VALID_JOURNEY_ID_NO_CAR_ASSIGNED = 200;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  MockMvc mvc;

  @Test
  public void whenCallStatus_shouldReturnOk() throws Exception {
    mvc.perform(get("/status")).andExpect(status().isOk());
  }

  @Test
  public void whenCarsAndInvalidJson_shouldReturnError() throws Exception {
    var peopleGroupDTO = new PeopleGroupDTO(1, 4);

    mvc.perform(put("/cars").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(peopleGroupDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void whenCarsAndInvalidCarMaxSeatSize_shouldReturnError() throws Exception {
    var cars = List.of(
        new CarDTO(1, INVALID_MAX_SEAT_SIZE),
        new CarDTO(2, 5));

    mvc.perform(put("/cars").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cars)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void whenCarsAndInvalidCarMinSeatSize_shouldReturnError() throws Exception {
    var cars = List.of(
        new CarDTO(1, INVALID_MIN_SEAT_SIZE),
        new CarDTO(2, 5));

    mvc.perform(put("/cars").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cars)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void whenCarsAndDuplicatedIdCar_shouldReturnError() throws Exception {
    var cars = List.of(
        new CarDTO(1,4),
        new CarDTO(1, 5));

    mvc.perform(put("/cars").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cars)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void whenJourneyAndInvalidJson_shouldReturnError() throws Exception {
    var cars = List.of(
        new CarDTO(1,4),
        new CarDTO(2, 5));

    mvc.perform(post("/journey").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cars)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void whenJourneyAndInvalidJourneyId_shouldReturnError() throws Exception {

    var peopleGroupDTO = new PeopleGroupDTO(INVALID_JOURNEY_ID, 4);

    mvc.perform(post("/journey").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(peopleGroupDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void whenJourneyInvalidJourneyMaxPassengers_shouldReturnError() throws Exception {
    var peopleGroupDTO = new PeopleGroupDTO(1, INVALID_MAX_PASSENGERS);

    mvc.perform(post("/journey").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(peopleGroupDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void whenJourneyInvalidJourneyMinPassengers_shouldReturnError() throws Exception {
    var peopleGroupDTO = new PeopleGroupDTO(1, INVALID_MIN_PASSENGERS);

    mvc.perform(post("/journey").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(peopleGroupDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void whenDropoffAndInvalidFormat_shouldReturnError() throws Exception {
    var peopleGroupDTO = new PeopleGroupDTO(1, 6);

    mvc.perform(post("/dropoff").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(peopleGroupDTO)))
        .andExpect(status().isUnsupportedMediaType());
  }

  @Test
  public void whenDropoffAndJourneyNotFound_shouldReturnError() throws Exception {
    mvc.perform(post("/dropoff").contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content("ID=" + VALID_JOURNEY_NOT_FOUND_ID))
        .andExpect(status().isNotFound());
  }

  @Test
  public void whenDropoffAndNoCarAssigned_shouldReturnNoContent() throws Exception {
    var cars = List.of(
        new CarDTO(20, 4));

    var peopleGroupDTO = new PeopleGroupDTO(VALID_JOURNEY_ID, 6);

    mvc.perform(put("/cars").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cars)))
        .andExpect(status().isOk());

    mvc.perform(post("/journey").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(peopleGroupDTO)))
        .andExpect(status().isOk());

    mvc.perform(post("/dropoff").contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content("ID=" + VALID_JOURNEY_ID))
        .andExpect(status().isNoContent());
  }

  @Test
  public void whenLocateAndInvalidUrlEncoded_shouldReturnError() throws Exception {
    var peopleGroupDTO = new PeopleGroupDTO(VALID_JOURNEY_ID, 6);

    mvc.perform(post("/locate").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(peopleGroupDTO)))
        .andExpect(status().isUnsupportedMediaType());
  }

  @Test
  public void whenLocateAndInvalidJourneyId_shouldReturnError() throws Exception {
    mvc.perform(post("/locate").contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content("ID=" + INVALID_JOURNEY_ID))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void whenLocateAndJourneyNotFound_shouldReturnError() throws Exception {
    mvc.perform(post("/locate").contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content("ID=" + VALID_JOURNEY_NOT_FOUND_ID))
        .andExpect(status().isNotFound());
  }

  @Test
  public void whenLocateAndNoCarAssigned_shouldReturnNoContent() throws Exception {
    var peopleGroupDTO = new PeopleGroupDTO(VALID_JOURNEY_ID_NO_CAR_ASSIGNED, 6);

    mvc.perform(post("/journey").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(peopleGroupDTO)))
        .andExpect(status().isOk());

    mvc.perform(post("/locate").contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content("ID=" + VALID_JOURNEY_ID_NO_CAR_ASSIGNED))
        .andExpect(status().isNoContent());
  }


  @Test
  public void whenJourneyHasCar_shouldBeLocatedAndDropoffOk() throws Exception {

    var cars = List.of(
        new CarDTO(1, 4),
        new CarDTO(2, 5));
    var peopleGroupDTO = new PeopleGroupDTO(1, 4);

    mvc.perform(put("/cars").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cars)))
        .andExpect(status().isOk());

    mvc.perform(post("/journey").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(peopleGroupDTO)))
        .andExpect(status().isOk());

    mvc.perform(post("/locate").contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content("ID=" + peopleGroupDTO.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.seats").value(4));

    mvc.perform(post("/dropoff").contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content("ID=" + peopleGroupDTO.getId()))
        .andExpect(status().isOk());
  }
}

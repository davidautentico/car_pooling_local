package com.drosa.cabify.carpooling;

import java.util.List;

import com.drosa.cabify.carpooling.domain.dtos.CarDTO;
import com.drosa.cabify.carpooling.domain.dtos.PeopleGroupDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class CarPoolingApplicationIntegrationTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  MockMvc mvc;

  @Test
  public void whenCallStatus_shouldReturnOk() throws Exception {
    mvc.perform(get("/status")).andExpect(status().isOk());
  }

  @Test
  public void whenJourneyHasCarAndDropoff_shouldReturnOk() throws Exception {

    var cars = List.of(
        new CarDTO(1, 4),
        new CarDTO(2, 6));
    var peopleGroupDTO = new PeopleGroupDTO(1, 4);

    mvc.perform(put("/cars").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cars)))
        .andExpect(status().isOk());

    mvc.perform(post("/journey").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(peopleGroupDTO)))
        .andExpect(status().isOk());

    mvc.perform(post("/dropoff").contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content("ID=" + peopleGroupDTO.getId()))
        .andExpect(status().isOk());
  }
}

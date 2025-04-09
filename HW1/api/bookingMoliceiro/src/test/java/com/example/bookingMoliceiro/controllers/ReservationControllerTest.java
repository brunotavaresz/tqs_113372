package com.example.bookingMoliceiro.controllers;

import com.example.bookingMoliceiro.models.Reservation;
import com.example.bookingMoliceiro.services.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Reservation reservation1;
    private Reservation reservation2;
    private List<Reservation> reservationList;

    @BeforeEach
    void setUp() {
        reservation1 = new Reservation();
        reservation1.setId(1L);
        reservation1.setReservationCode("RES123");

        reservation2 = new Reservation();
        reservation2.setId(2L);
        reservation2.setReservationCode("RES456");

        reservationList = Arrays.asList(reservation1, reservation2);
    }

    @Test
    void createReservation_ShouldCreateAndReturnReservation() throws Exception {
        // Arrange
        when(reservationService.createReservation(any(Reservation.class))).thenReturn(reservation1);

        // Act & Assert
        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservation1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.reservationCode", is("RES123")));
    }

    @Test
    void getAllReservations_ShouldReturnAllReservations() throws Exception {
        // Arrange
        when(reservationService.getAllReservations()).thenReturn(reservationList);

        // Act & Assert
        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].reservationCode", is("RES123")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].reservationCode", is("RES456")));
    }

    @Test
    void getReservationByCode_ShouldReturnReservation() throws Exception {
        // Arrange
        String reservationCode = "RES123";
        when(reservationService.getReservationByCode(reservationCode)).thenReturn(reservation1);

        // Act & Assert
        mockMvc.perform(get("/reservations/{reservationCode}", reservationCode))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.reservationCode", is("RES123")));
    }

    @Test
    void cancelReservation_ShouldCancelReservation() throws Exception {
        // Arrange
        String reservationCode = "RES123";
        doNothing().when(reservationService).cancelReservation(reservationCode);

        // Act & Assert
        mockMvc.perform(delete("/reservations/{reservationCode}", reservationCode))
                .andExpect(status().isOk());

        verify(reservationService, times(1)).cancelReservation(reservationCode);
    }

    @Test
    void checkIn_ShouldReturnSuccessMessage() throws Exception {
        // Arrange
        String reservationCode = "RES123";
        when(reservationService.checkInReservation(reservationCode)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/reservations/{reservationCode}/check-in", reservationCode))
                .andExpect(status().isOk())
                .andExpect(content().string("Reserva realizada com sucesso."));
    }

    @Test
    void checkIn_ShouldReturnFailureMessage() throws Exception {
        // Arrange
        String reservationCode = "RES999";
        when(reservationService.checkInReservation(reservationCode)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/reservations/{reservationCode}/check-in", reservationCode))
                .andExpect(status().isOk())
                .andExpect(content().string("Falha ao realizar check-in."));
    }

    @Test
    void updateReservation_ShouldUpdateAndReturnReservation() throws Exception {
        // Arrange
        String reservationCode = "RES123";
        Reservation updatedReservation = new Reservation();
        updatedReservation.setId(1L);
        updatedReservation.setReservationCode(reservationCode);

        when(reservationService.updateReservation(eq(reservationCode), any(Reservation.class))).thenReturn(updatedReservation);

        // Act & Assert
        mockMvc.perform(put("/reservations/{reservationCode}", reservationCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedReservation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.reservationCode", is("RES123")));
    }
}
package com.example.bookingMoliceiro.controllers;

import com.example.bookingMoliceiro.exceptions.RestaurantCapacityExceededException;
import com.example.bookingMoliceiro.models.Meal;
import com.example.bookingMoliceiro.models.Reservation;
import com.example.bookingMoliceiro.models.Restaurant;
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
import java.util.NoSuchElementException;

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
    private Restaurant restaurant;
    private Meal meal;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Restaurante Teste");

        meal = new Meal();
        meal.setId(1L);
        meal.setName("Refeição Teste");

        reservation1 = new Reservation();
        reservation1.setId(1L);
        reservation1.setReservationCode("RES123");
        reservation1.setCustomerName("João Silva");
        reservation1.setRestaurant(restaurant);
        reservation1.setMeal(meal);
        reservation1.setCheckedIn(false);

        reservation2 = new Reservation();
        reservation2.setId(2L);
        reservation2.setReservationCode("RES456");
        reservation2.setCustomerName("Maria Oliveira");
        reservation2.setRestaurant(restaurant);
        reservation2.setMeal(meal);
        reservation2.setCheckedIn(false);

        reservationList = Arrays.asList(reservation1, reservation2);
    }

    @Test
    void createReservation_Success() throws Exception {
        when(reservationService.createReservation(any(Reservation.class))).thenReturn(reservation1);

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservation1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.reservationCode", is("RES123")))
                .andExpect(jsonPath("$.customerName", is("João Silva")))
                .andExpect(jsonPath("$.checkedIn", is(false)));

        verify(reservationService, times(1)).createReservation(any(Reservation.class));
    }

    @Test
    void createReservation_CapacityExceeded() throws Exception {
        when(reservationService.createReservation(any(Reservation.class)))
                .thenThrow(new RestaurantCapacityExceededException("Capacidade do restaurante excedida"));

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservation1)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("Capacidade do restaurante excedida")));

        verify(reservationService, times(1)).createReservation(any(Reservation.class));
    }

    @Test
    void createReservation_GenericException() throws Exception {
        when(reservationService.createReservation(any(Reservation.class)))
                .thenThrow(new RuntimeException("Erro genérico"));

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservation1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Erro ao criar reserva: Erro genérico")));

        verify(reservationService, times(1)).createReservation(any(Reservation.class));
    }

    @Test
    void getAllReservations_Success() throws Exception {
        when(reservationService.getAllReservations()).thenReturn(reservationList);

        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].reservationCode", is("RES123")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].reservationCode", is("RES456")));

        verify(reservationService, times(1)).getAllReservations();
    }

    @Test
    void getReservationByCode_Success() throws Exception {
        String reservationCode = "RES123";
        when(reservationService.getReservationByCode(reservationCode)).thenReturn(reservation1);

        mockMvc.perform(get("/reservations/{reservationCode}", reservationCode))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.reservationCode", is("RES123")))
                .andExpect(jsonPath("$.customerName", is("João Silva")));

        verify(reservationService, times(1)).getReservationByCode(reservationCode);
    }

    @Test
    void getReservationByCode_NotFound() throws Exception {
        String reservationCode = "NONEXISTENT";
        when(reservationService.getReservationByCode(reservationCode))
                .thenThrow(new NoSuchElementException("Reserva não encontrada"));

        mockMvc.perform(get("/reservations/{reservationCode}", reservationCode))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Reserva não encontrada")));

        verify(reservationService, times(1)).getReservationByCode(reservationCode);
    }

    @Test
    void cancelReservation_Success() throws Exception {
        String reservationCode = "RES123";
        doNothing().when(reservationService).cancelReservation(reservationCode);

        mockMvc.perform(delete("/reservations/{reservationCode}", reservationCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Reserva cancelada com sucesso")));

        verify(reservationService, times(1)).cancelReservation(reservationCode);
    }

    @Test
    void cancelReservation_NotFound() throws Exception {
        String reservationCode = "NONEXISTENT";
        doThrow(new NoSuchElementException("Reserva não encontrada"))
                .when(reservationService).cancelReservation(reservationCode);

        mockMvc.perform(delete("/reservations/{reservationCode}", reservationCode))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Reserva não encontrada")));

        verify(reservationService, times(1)).cancelReservation(reservationCode);
    }

    @Test
    void checkIn_Success() throws Exception {
        String reservationCode = "RES123";
        when(reservationService.checkInReservation(reservationCode)).thenReturn(true);

        mockMvc.perform(post("/reservations/{reservationCode}/check-in", reservationCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Check-in realizado com sucesso")));

        verify(reservationService, times(1)).checkInReservation(reservationCode);
    }

    @Test
    void checkIn_AlreadyCheckedIn() throws Exception {
        String reservationCode = "RES123";
        when(reservationService.checkInReservation(reservationCode)).thenReturn(false);

        mockMvc.perform(post("/reservations/{reservationCode}/check-in", reservationCode))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Reserva já possui check-in")));

        verify(reservationService, times(1)).checkInReservation(reservationCode);
    }

    @Test
    void checkIn_NotFound() throws Exception {
        String reservationCode = "NONEXISTENT";
        when(reservationService.checkInReservation(reservationCode))
                .thenThrow(new NoSuchElementException("Reserva não encontrada"));

        mockMvc.perform(post("/reservations/{reservationCode}/check-in", reservationCode))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Reserva não encontrada")));

        verify(reservationService, times(1)).checkInReservation(reservationCode);
    }

    @Test
    void updateReservation_Success() throws Exception {
        String reservationCode = "RES123";
        when(reservationService.updateReservation(eq(reservationCode), any(Reservation.class)))
                .thenReturn(reservation1);

        mockMvc.perform(put("/reservations/{reservationCode}", reservationCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservation1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.reservationCode", is("RES123")))
                .andExpect(jsonPath("$.customerName", is("João Silva")));

        verify(reservationService, times(1)).updateReservation(eq(reservationCode), any(Reservation.class));
    }

    @Test
    void updateReservation_CapacityExceeded() throws Exception {
        String reservationCode = "RES123";
        when(reservationService.updateReservation(eq(reservationCode), any(Reservation.class)))
                .thenThrow(new RestaurantCapacityExceededException("Capacidade do restaurante excedida"));

        mockMvc.perform(put("/reservations/{reservationCode}", reservationCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservation1)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("Capacidade do restaurante excedida")));

        verify(reservationService, times(1)).updateReservation(eq(reservationCode), any(Reservation.class));
    }

    @Test
    void updateReservation_NotFound() throws Exception {
        String reservationCode = "NONEXISTENT";
        when(reservationService.updateReservation(eq(reservationCode), any(Reservation.class)))
                .thenThrow(new NoSuchElementException("Reserva não encontrada"));

        mockMvc.perform(put("/reservations/{reservationCode}", reservationCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservation1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Reserva não encontrada")));

        verify(reservationService, times(1)).updateReservation(eq(reservationCode), any(Reservation.class));
    }

    @Test
    void handleCapacityExceededException() throws Exception {
        String reservationCode = "RES123";
        when(reservationService.updateReservation(eq(reservationCode), any(Reservation.class)))
                .thenThrow(new RestaurantCapacityExceededException("Capacidade do restaurante excedida"));

        mockMvc.perform(put("/reservations/{reservationCode}", reservationCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservation1)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("Capacidade do restaurante excedida")));
    }
}
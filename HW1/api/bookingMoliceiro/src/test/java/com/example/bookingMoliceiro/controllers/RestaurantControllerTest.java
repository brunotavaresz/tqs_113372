package com.example.bookingMoliceiro.controllers;

import com.example.bookingMoliceiro.models.Meal;
import com.example.bookingMoliceiro.models.Reservation;
import com.example.bookingMoliceiro.models.Restaurant;
import com.example.bookingMoliceiro.services.MealService;
import com.example.bookingMoliceiro.services.ReservationService;
import com.example.bookingMoliceiro.services.RestaurantService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
public class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private MealService mealService;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Restaurant restaurant1;
    private Restaurant restaurant2;
    private List<Restaurant> restaurantList;
    private Meal meal1;
    private Meal meal2;
    private List<Meal> mealList;
    private Reservation reservation1;
    private Reservation reservation2;
    private List<Reservation> reservationList;

    @BeforeEach
    void setUp() {
        // Set up restaurants
        restaurant1 = new Restaurant();
        restaurant1.setId(1L);
        restaurant1.setName("O Bairro");
        restaurant1.setLocation("Aveiro Centro");

        restaurant2 = new Restaurant();
        restaurant2.setId(2L);
        restaurant2.setName("Ramona");
        restaurant2.setLocation("Rossio");

        restaurantList = Arrays.asList(restaurant1, restaurant2);

        // Set up meals
        meal1 = new Meal();
        meal1.setId(1L);
        meal1.setName("Bacalhau à Brás");
        meal1.setPrice(15.50);

        meal2 = new Meal();
        meal2.setId(2L);
        meal2.setName("Francesinha");
        meal2.setPrice(12.75);

        mealList = Arrays.asList(meal1, meal2);

        // Set up reservations
        reservation1 = new Reservation();
        reservation1.setId(1L);
        reservation1.setReservationCode("RES123");

        reservation2 = new Reservation();
        reservation2.setId(2L);
        reservation2.setReservationCode("RES456");

        reservationList = Arrays.asList(reservation1, reservation2);
    }

    @Test
    void getAllRestaurants_ShouldReturnAllRestaurants() throws Exception {
        // Arrange
        when(restaurantService.getAllRestaurants()).thenReturn(restaurantList);

        // Act & Assert
        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("O Bairro")))
                .andExpect(jsonPath("$[0].location", is("Aveiro Centro")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Ramona")))
                .andExpect(jsonPath("$[1].location", is("Rossio")));
    }

    @Test
    void createRestaurant_ShouldCreateAndReturnRestaurant() throws Exception {
        // Arrange
        when(restaurantService.saveRestaurant(any(Restaurant.class))).thenReturn(restaurant1);

        // Act & Assert
        mockMvc.perform(post("/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurant1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("O Bairro")))
                .andExpect(jsonPath("$.location", is("Aveiro Centro")));
    }

    @Test
    void getRestaurantById_ShouldReturnRestaurant() throws Exception {
        // Arrange
        Long restaurantId = 1L;
        when(restaurantService.getRestaurantById(restaurantId)).thenReturn(restaurant1);

        // Act & Assert
        mockMvc.perform(get("/restaurants/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("O Bairro")))
                .andExpect(jsonPath("$.location", is("Aveiro Centro")));
    }

    @Test
    void updateRestaurant_ShouldUpdateAndReturnRestaurant() throws Exception {
        // Arrange
        Long restaurantId = 1L;
        Restaurant updatedRestaurant = new Restaurant();
        updatedRestaurant.setId(restaurantId);
        updatedRestaurant.setName("O Bairro Novo");
        updatedRestaurant.setLocation("Praça do Peixe");

        when(restaurantService.updateRestaurant(eq(restaurantId), any(Restaurant.class))).thenReturn(updatedRestaurant);

        // Act & Assert
        mockMvc.perform(put("/restaurants/{restaurantId}", restaurantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRestaurant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("O Bairro Novo")))
                .andExpect(jsonPath("$.location", is("Praça do Peixe")));
    }

    @Test
    void getMenusByRestaurant_ShouldReturnRestaurantMeals() throws Exception {
        // Arrange
        Long restaurantId = 1L;
        when(mealService.getMenusByRestaurant(restaurantId)).thenReturn(mealList);

        // Act & Assert
        mockMvc.perform(get("/restaurants/{restaurantId}/menus", restaurantId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Bacalhau à Brás")))
                .andExpect(jsonPath("$[0].price", is(15.5)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Francesinha")))
                .andExpect(jsonPath("$[1].price", is(12.75)));
    }

    @Test
    void getReservationsByRestaurant_ShouldReturnRestaurantReservations() throws Exception {
        // Arrange
        Long restaurantId = 1L;
        when(reservationService.getReservationsByRestaurant(restaurantId)).thenReturn(reservationList);

        // Act & Assert
        mockMvc.perform(get("/restaurants/{restaurantId}/reservations", restaurantId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].reservationCode", is("RES123")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].reservationCode", is("RES456")));
    }
}
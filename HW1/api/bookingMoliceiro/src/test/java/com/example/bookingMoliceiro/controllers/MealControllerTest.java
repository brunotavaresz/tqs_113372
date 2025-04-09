package com.example.bookingMoliceiro.controllers;

import com.example.bookingMoliceiro.models.Meal;
import com.example.bookingMoliceiro.services.MealService;
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

@WebMvcTest(MealController.class)
public class MealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MealService mealService;

    @Autowired
    private ObjectMapper objectMapper;

    private Meal meal1;
    private Meal meal2;
    private List<Meal> mealList;

    @BeforeEach
    void setUp() {
        meal1 = new Meal();
        meal1.setId(1L);
        meal1.setName("Bacalhau à Brás");
        meal1.setPrice(15.50);

        meal2 = new Meal();
        meal2.setId(2L);
        meal2.setName("Francesinha");
        meal2.setPrice(12.75);

        mealList = Arrays.asList(meal1, meal2);
    }

    @Test
    void getAllMeals_ShouldReturnAllMeals() throws Exception {
        // Arrange
        when(mealService.getAllMeals()).thenReturn(mealList);

        // Act & Assert
        mockMvc.perform(get("/meals"))
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
    void createMeal_ShouldCreateAndReturnMeal() throws Exception {
        // Arrange
        when(mealService.saveMeal(any(Meal.class))).thenReturn(meal1);

        // Act & Assert
        mockMvc.perform(post("/meals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(meal1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Bacalhau à Brás")))
                .andExpect(jsonPath("$.price", is(15.5)));
    }

    @Test
    void getMealById_ShouldReturnMeal() throws Exception {
        // Arrange
        Long mealId = 1L;
        when(mealService.getMealById(mealId)).thenReturn(meal1);

        // Act & Assert
        mockMvc.perform(get("/meals/{mealId}", mealId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Bacalhau à Brás")))
                .andExpect(jsonPath("$.price", is(15.5)));
    }

    @Test
    void updateMeal_ShouldUpdateAndReturnMeal() throws Exception {
        // Arrange
        Long mealId = 1L;
        Meal updatedMeal = new Meal();
        updatedMeal.setId(mealId);
        updatedMeal.setName("Bacalhau à Gomes de Sá");
        updatedMeal.setPrice(16.75);

        when(mealService.updateMeal(eq(mealId), any(Meal.class))).thenReturn(updatedMeal);

        // Act & Assert
        mockMvc.perform(put("/meals/{mealId}", mealId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMeal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Bacalhau à Gomes de Sá")))
                .andExpect(jsonPath("$.price", is(16.75)));
    }
}
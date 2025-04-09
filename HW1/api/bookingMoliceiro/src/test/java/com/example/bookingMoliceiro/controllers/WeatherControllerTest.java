package com.example.bookingMoliceiro.controllers;

import com.example.bookingMoliceiro.models.Meal;
import com.example.bookingMoliceiro.services.MealService;
import com.example.bookingMoliceiro.services.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeatherController.class)
public class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @MockBean
    private MealService mealService;

    private Meal meal;
    private Map<String, Object> weatherForecast;
    private Map<String, Object> cacheStats;

    @BeforeEach
    void setUp() {
        meal = new Meal();
        meal.setId(1L);
        meal.setName("Bacalhau à Brás");
        meal.setPrice(15.50);

        weatherForecast = new HashMap<>();
        weatherForecast.put("temperature", 22.5);
        weatherForecast.put("description", "Mostly sunny");
        weatherForecast.put("humidity", 65);

        cacheStats = new HashMap<>();
        cacheStats.put("size", 10);
        cacheStats.put("hitCount", 25);
        cacheStats.put("missCount", 5);
    }

    @Test
    void getWeatherForMeal_ShouldReturnWeatherForecast() throws Exception {
        // Arrange
        Long mealId = 1L;
        when(mealService.getMealById(mealId)).thenReturn(meal);
        when(weatherService.getWeatherForecastForMeal(any(Meal.class))).thenReturn(weatherForecast);

        // Act & Assert
        mockMvc.perform(get("/api/meals/{mealId}/weather", mealId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.temperature", is(22.5)))
                .andExpect(jsonPath("$.description", is("Mostly sunny")))
                .andExpect(jsonPath("$.humidity", is(65)));
    }

    @Test
    void getWeatherForMeal_WhenMealNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        Long mealId = 99L;
        when(mealService.getMealById(mealId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/meals/{mealId}/weather", mealId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCacheStatistics_ShouldReturnCacheStats() throws Exception {
        // Arrange
        when(weatherService.getCacheStatistics()).thenReturn(cacheStats);

        // Act & Assert
        mockMvc.perform(get("/api/weather/cache/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.hitCount", is(25)))
                .andExpect(jsonPath("$.missCount", is(5)));
    }

    @Test
    void clearCache_ShouldClearCacheAndReturnOk() throws Exception {
        // Arrange
        doNothing().when(weatherService).clearCache();

        // Act & Assert
        mockMvc.perform(post("/api/weather/cache/clear"))
                .andExpect(status().isOk());

        verify(weatherService, times(1)).clearCache();
    }
}
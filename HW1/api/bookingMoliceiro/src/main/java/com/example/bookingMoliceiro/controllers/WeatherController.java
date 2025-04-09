package com.example.bookingMoliceiro.controllers;

import com.example.bookingMoliceiro.models.Meal;
import com.example.bookingMoliceiro.services.MealService;
import com.example.bookingMoliceiro.services.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WeatherController {
    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);
    private final WeatherService weatherService;
    private final MealService mealService;
    
    @Autowired
    public WeatherController(WeatherService weatherService, MealService mealService) {
        this.weatherService = weatherService;
        this.mealService = mealService;
    }
    
    @GetMapping("/meals/{mealId}/weather")
    public ResponseEntity<Map<String, Object>> getWeatherForMeal(@PathVariable Long mealId) {
        logger.info("Fetching weather forecast for meal with ID: {}", mealId);
        // Get meal by ID
        Meal meal = mealService.getMealById(mealId);
        // Check if meal exists
        if (meal == null) {
            logger.warn("Meal with ID {} not found", mealId);
            return ResponseEntity.notFound().build();
        }
        // Get weather forecast for the meal
        Map<String, Object> weatherForecast = weatherService.getWeatherForecastForMeal(meal);
        logger.info("Weather forecast retrieved successfully for meal with ID: {}", mealId);
        return ResponseEntity.ok(weatherForecast);
    }
    
    /**
     * Endpoint to get cache statistics
     */
    @GetMapping("/weather/cache/stats")
    public ResponseEntity<Map<String, Object>> getCacheStatistics() {
        logger.info("Fetching weather cache statistics.");
        Map<String, Object> stats = weatherService.getCacheStatistics();
        logger.info("Cache statistics retrieved: {}", stats);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Endpoint to clear the cache
     */
    @PostMapping("/weather/cache/clear")
    public ResponseEntity<Void> clearCache() {
        logger.info("Clearing the weather cache.");
        weatherService.clearCache();
        logger.info("Weather cache cleared successfully.");
        return ResponseEntity.ok().build();
    }
}
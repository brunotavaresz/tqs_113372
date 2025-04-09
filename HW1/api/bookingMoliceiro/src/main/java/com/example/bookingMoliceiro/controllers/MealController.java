package com.example.bookingMoliceiro.controllers;

import com.example.bookingMoliceiro.exceptions.InvalidMealTimeException;
import com.example.bookingMoliceiro.models.Meal;
import com.example.bookingMoliceiro.services.MealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/meals")
public class MealController {
    private static final Logger logger = LoggerFactory.getLogger(MealController.class);
    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @GetMapping
    public List<Meal> getAllMeals() {
        logger.info("Fetching all meals.");
        List<Meal> meals = mealService.getAllMeals();
        logger.info("Found {} meals.", meals.size());
        return meals;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createMeal(@RequestBody Meal meal) {
        try {
            logger.info("Creating a new meal: {}", meal.getName());
            Meal createdMeal = mealService.saveMeal(meal);
            logger.info("Meal created with ID: {}", createdMeal.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMeal);
        } catch (InvalidMealTimeException e) {
            logger.warn("Invalid meal time error: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/{mealId}")
    public Meal getMealById(@PathVariable Long mealId) {
        logger.info("Fetching meal with ID: {}", mealId);
        Meal meal = mealService.getMealById(mealId);
        logger.info("Meal found: {}", meal.getName());
        return meal;
    }

    @PutMapping("/{mealId}")
    public ResponseEntity<?> updateMeal(@PathVariable Long mealId, @RequestBody Meal meal) {
        try {
            logger.info("Updating meal with ID: {}", mealId);
            Meal updatedMeal = mealService.updateMeal(mealId, meal);
            logger.info("Meal updated: {}", updatedMeal.getName());
            return ResponseEntity.ok(updatedMeal);
        } catch (InvalidMealTimeException e) {
            logger.warn("Invalid meal time error when updating: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    // Tratamento de exceção global (opcional, mas recomendado)
    @ExceptionHandler(InvalidMealTimeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidMealTimeException(InvalidMealTimeException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
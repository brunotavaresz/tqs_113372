package com.example.bookingMoliceiro.controllers;

import com.example.bookingMoliceiro.models.Meal;
import com.example.bookingMoliceiro.services.MealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Meal createMeal(@RequestBody Meal meal) {
        logger.info("Creating a new meal: {}", meal.getName());
        Meal createdMeal = mealService.saveMeal(meal);
        logger.info("Meal created with ID: {}", createdMeal.getId());
        return createdMeal;
    }

    @GetMapping("/{mealId}")
    public Meal getMealById(@PathVariable Long mealId) {
        logger.info("Fetching meal with ID: {}", mealId);
        Meal meal = mealService.getMealById(mealId);
        logger.info("Meal found: {}", meal.getName());
        return meal;
    }

    @PutMapping("/{mealId}")
    public Meal updateMeal(@PathVariable Long mealId, @RequestBody Meal meal) {
        logger.info("Updating meal with ID: {}", mealId);
        Meal updatedMeal = mealService.updateMeal(mealId, meal);
        logger.info("Meal updated: {}", updatedMeal.getName());
        return updatedMeal;
    }
}

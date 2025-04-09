package com.example.bookingMoliceiro.services;

import com.example.bookingMoliceiro.models.Meal;
import com.example.bookingMoliceiro.repositories.MealRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MealService {
    private static final Logger logger = LoggerFactory.getLogger(MealService.class);  // Logger criado

    private final MealRepository mealRepository;

    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    public List<Meal> getAllMeals() {
        logger.info("Fetching all meals.");
        return mealRepository.findAll();
    }

    public Meal saveMeal(Meal meal) {
        logger.info("Saving meal: {}", meal.getName());
        return mealRepository.save(meal);
    }

    public Meal getMealById(Long mealId) {
        logger.info("Fetching meal with ID: {}", mealId);
        return mealRepository.findById(mealId)
                .orElseThrow(() -> {
                    logger.warn("Meal with ID {} not found", mealId);
                    return new IllegalArgumentException("Refeição não encontrada");
                });
    }

    public Meal updateMeal(Long mealId, Meal meal) {
        logger.info("Updating meal with ID: {}", mealId);
        Meal existingMeal = getMealById(mealId);
        existingMeal.setName(meal.getName());
        existingMeal.setPrice(meal.getPrice());
        return mealRepository.save(existingMeal);
    }

    public List<Meal> getMenusByRestaurant(Long restaurantId) {
        logger.info("Fetching meals for restaurant with ID: {}", restaurantId);
        return mealRepository.findByRestaurantId(restaurantId);
    }
}
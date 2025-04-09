package com.example.bookingMoliceiro.services;

import com.example.bookingMoliceiro.exceptions.InvalidMealTimeException;
import com.example.bookingMoliceiro.models.Meal;
import com.example.bookingMoliceiro.repositories.MealRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class MealService {
    private static final Logger logger = LoggerFactory.getLogger(MealService.class);
    private final MealRepository mealRepository;

    // Definir os horários permitidos
    private static final LocalTime ALMOCO_INICIO = LocalTime.of(11, 0);
    private static final LocalTime ALMOCO_FIM = LocalTime.of(15, 0);
    private static final LocalTime JANTAR_INICIO = LocalTime.of(18, 0);
    private static final LocalTime JANTAR_FIM = LocalTime.of(23, 0);

    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    public List<Meal> getAllMeals() {
        logger.info("Fetching all meals.");
        return mealRepository.findAll();
    }

    public Meal saveMeal(Meal meal) {
        logger.info("Saving meal: {}", meal.getName());
        
        // Validar horário da refeição
        validateMealTime(meal.getTime());
        
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
        
        // Atualizar o nome e preço
        existingMeal.setName(meal.getName());
        existingMeal.setPrice(meal.getPrice());
        existingMeal.setDate(meal.getDate());
        existingMeal.setRestaurant(meal.getRestaurant());
        
        // Validar e atualizar o horário apenas se não for nulo
        if (meal.getTime() != null) {
            validateMealTime(meal.getTime());
            existingMeal.setTime(meal.getTime());
        }
        // Se o time for nulo, mantém o horário existente
        
        return mealRepository.save(existingMeal);
    }
    

    public List<Meal> getMenusByRestaurant(Long restaurantId) {
        logger.info("Fetching meals for restaurant with ID: {}", restaurantId);
        return mealRepository.findByRestaurantId(restaurantId);
    }
    
    /**
     * Valida se o horário está dentro dos períodos de almoço ou jantar
     * @param time Horário a ser validado
     * @throws InvalidMealTimeException se o horário não for válido
     */
    private void validateMealTime(LocalTime time) {
        if (time == null) {
            logger.warn("Meal time cannot be null");
            throw new InvalidMealTimeException("O horário da refeição não pode ser nulo");
        }
        
        boolean isLunchTime = !time.isBefore(ALMOCO_INICIO) && !time.isAfter(ALMOCO_FIM);
        boolean isDinnerTime = !time.isBefore(JANTAR_INICIO) && !time.isAfter(JANTAR_FIM);
        
        if (!isLunchTime && !isDinnerTime) {
            logger.warn("Invalid meal time: {}. Must be between 11:00-15:00 for lunch or 18:00-23:00 for dinner", time);
            throw new InvalidMealTimeException("O horário deve ser entre 11:00-15:00 para almoço ou 18:00-23:00 para jantar");
        }
    }
}
package com.example.bookingMoliceiro.services;

import com.example.bookingMoliceiro.models.Meal;
import com.example.bookingMoliceiro.repositories.MealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @InjectMocks
    private MealService mealService;

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
    void getAllMeals_ShouldReturnAllMeals() {
        // Arrange
        when(mealRepository.findAll()).thenReturn(mealList);

        // Act
        List<Meal> result = mealService.getAllMeals();

        // Assert
        assertEquals(2, result.size());
        assertEquals(meal1, result.get(0));
        assertEquals(meal2, result.get(1));
        verify(mealRepository, times(1)).findAll();
    }

    @Test
    void saveMeal_ShouldSaveMeal() {
        // Arrange
        when(mealRepository.save(any(Meal.class))).thenReturn(meal1);

        // Act
        Meal savedMeal = mealService.saveMeal(meal1);

        // Assert
        assertNotNull(savedMeal);
        assertEquals(meal1.getName(), savedMeal.getName());
        verify(mealRepository, times(1)).save(meal1);
    }

    @Test
    void getMealById_WhenExistingId_ShouldReturnMeal() {
        // Arrange
        Long mealId = 1L;
        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal1));

        // Act
        Meal foundMeal = mealService.getMealById(mealId);

        // Assert
        assertNotNull(foundMeal);
        assertEquals(mealId, foundMeal.getId());
        assertEquals(meal1.getName(), foundMeal.getName());
        verify(mealRepository, times(1)).findById(mealId);
    }

    @Test
    void getMealById_WhenNonExistingId_ShouldThrowException() {
        // Arrange
        Long mealId = 99L;
        when(mealRepository.findById(mealId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            mealService.getMealById(mealId);
        });

        assertEquals("Refeição não encontrada", exception.getMessage());
        verify(mealRepository, times(1)).findById(mealId);
    }

    @Test
    void updateMeal_WhenExistingId_ShouldUpdateMeal() {
        // Arrange
        Long mealId = 1L;
        Meal updatedMeal = new Meal();
        updatedMeal.setName("Bacalhau à Gomes de Sá");
        updatedMeal.setPrice(16.75);

        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal1));
        when(mealRepository.save(any(Meal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Meal result = mealService.updateMeal(mealId, updatedMeal);

        // Assert
        assertNotNull(result);
        assertEquals(updatedMeal.getName(), result.getName());
        assertEquals(updatedMeal.getPrice(), result.getPrice());
        assertEquals(mealId, result.getId());
        verify(mealRepository, times(1)).findById(mealId);
        verify(mealRepository, times(1)).save(any(Meal.class));
    }

    @Test
    void getMenusByRestaurant_ShouldReturnRestaurantMeals() {
        // Arrange
        Long restaurantId = 1L;
        when(mealRepository.findByRestaurantId(restaurantId)).thenReturn(mealList);

        // Act
        List<Meal> result = mealService.getMenusByRestaurant(restaurantId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(meal1, result.get(0));
        assertEquals(meal2, result.get(1));
        verify(mealRepository, times(1)).findByRestaurantId(restaurantId);
    }
}
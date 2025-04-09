package com.example.bookingMoliceiro.services;

import com.example.bookingMoliceiro.exceptions.InvalidMealTimeException;
import com.example.bookingMoliceiro.models.Meal;
import com.example.bookingMoliceiro.models.Restaurant;
import com.example.bookingMoliceiro.repositories.MealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Restaurante Teste");

        meal1 = new Meal();
        meal1.setId(1L);
        meal1.setName("Bacalhau à Brás");
        meal1.setPrice(15.50);
        meal1.setTime(LocalTime.of(13, 0)); // Horário de almoço válido
        meal1.setDate(LocalDate.now());
        meal1.setRestaurant(restaurant);

        meal2 = new Meal();
        meal2.setId(2L);
        meal2.setName("Francesinha");
        meal2.setPrice(12.75);
        meal2.setTime(LocalTime.of(20, 0)); // Horário de jantar válido
        meal2.setDate(LocalDate.now());
        meal2.setRestaurant(restaurant);

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
    void saveMeal_WithValidLunchTime_ShouldSaveMeal() {
        // Arrange
        Meal newMeal = new Meal();
        newMeal.setName("Cozido à Portuguesa");
        newMeal.setPrice(18.0);
        newMeal.setTime(LocalTime.of(12, 30)); // Horário de almoço válido
        newMeal.setDate(LocalDate.now());
        newMeal.setRestaurant(restaurant);

        when(mealRepository.save(any(Meal.class))).thenReturn(newMeal);

        // Act
        Meal savedMeal = mealService.saveMeal(newMeal);

        // Assert
        assertNotNull(savedMeal);
        assertEquals(newMeal.getName(), savedMeal.getName());
        verify(mealRepository, times(1)).save(newMeal);
    }

    @Test
    void saveMeal_WithValidDinnerTime_ShouldSaveMeal() {
        // Arrange
        Meal newMeal = new Meal();
        newMeal.setName("Polvo à Lagareiro");
        newMeal.setPrice(22.0);
        newMeal.setTime(LocalTime.of(19, 45)); // Horário de jantar válido
        newMeal.setDate(LocalDate.now());
        newMeal.setRestaurant(restaurant);

        when(mealRepository.save(any(Meal.class))).thenReturn(newMeal);

        // Act
        Meal savedMeal = mealService.saveMeal(newMeal);

        // Assert
        assertNotNull(savedMeal);
        assertEquals(newMeal.getName(), savedMeal.getName());
        verify(mealRepository, times(1)).save(newMeal);
    }

    @Test
    void saveMeal_WithNullTime_ShouldThrowException() {
        // Arrange
        Meal newMeal = new Meal();
        newMeal.setName("Arroz de Marisco");
        newMeal.setPrice(25.0);
        newMeal.setTime(null); // Tempo nulo
        newMeal.setDate(LocalDate.now());
        newMeal.setRestaurant(restaurant);

        // Act & Assert
        Exception exception = assertThrows(InvalidMealTimeException.class, () -> {
            mealService.saveMeal(newMeal);
        });

        assertEquals("O horário da refeição não pode ser nulo", exception.getMessage());
        verify(mealRepository, never()).save(any(Meal.class));
    }

    @Test
    void saveMeal_WithInvalidTimeBetweenMeals_ShouldThrowException() {
        // Arrange
        Meal newMeal = new Meal();
        newMeal.setName("Amêijoas à Bulhão Pato");
        newMeal.setPrice(16.0);
        newMeal.setTime(LocalTime.of(16, 30)); // Horário entre almoço e jantar (inválido)
        newMeal.setDate(LocalDate.now());
        newMeal.setRestaurant(restaurant);

        // Act & Assert
        Exception exception = assertThrows(InvalidMealTimeException.class, () -> {
            mealService.saveMeal(newMeal);
        });

        assertEquals("O horário deve ser entre 11:00-15:00 para almoço ou 18:00-23:00 para jantar", exception.getMessage());
        verify(mealRepository, never()).save(any(Meal.class));
    }

    @Test
    void saveMeal_WithInvalidTimeBeforeLunch_ShouldThrowException() {
        // Arrange
        Meal newMeal = new Meal();
        newMeal.setName("Pequeno Almoço Português");
        newMeal.setPrice(8.0);
        newMeal.setTime(LocalTime.of(8, 0)); // Horário antes do almoço (inválido)
        newMeal.setDate(LocalDate.now());
        newMeal.setRestaurant(restaurant);

        // Act & Assert
        Exception exception = assertThrows(InvalidMealTimeException.class, () -> {
            mealService.saveMeal(newMeal);
        });

        assertEquals("O horário deve ser entre 11:00-15:00 para almoço ou 18:00-23:00 para jantar", exception.getMessage());
        verify(mealRepository, never()).save(any(Meal.class));
    }

    @Test
    void saveMeal_WithInvalidTimeAfterDinner_ShouldThrowException() {
        // Arrange
        Meal newMeal = new Meal();
        newMeal.setName("Ceia Tardia");
        newMeal.setPrice(12.0);
        newMeal.setTime(LocalTime.of(23, 30)); // Horário depois do jantar (inválido)
        newMeal.setDate(LocalDate.now());
        newMeal.setRestaurant(restaurant);

        // Act & Assert
        Exception exception = assertThrows(InvalidMealTimeException.class, () -> {
            mealService.saveMeal(newMeal);
        });

        assertEquals("O horário deve ser entre 11:00-15:00 para almoço ou 18:00-23:00 para jantar", exception.getMessage());
        verify(mealRepository, never()).save(any(Meal.class));
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
    void updateMeal_WithValidTime_ShouldUpdateMeal() {
        // Arrange
        Long mealId = 1L;
        Meal updatedMeal = new Meal();
        updatedMeal.setName("Bacalhau à Gomes de Sá");
        updatedMeal.setPrice(16.75);
        updatedMeal.setTime(LocalTime.of(14, 0)); // Horário de almoço válido
        updatedMeal.setDate(LocalDate.now());
        updatedMeal.setRestaurant(restaurant);

        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal1));
        when(mealRepository.save(any(Meal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Meal result = mealService.updateMeal(mealId, updatedMeal);

        // Assert
        assertNotNull(result);
        assertEquals(updatedMeal.getName(), result.getName());
        assertEquals(updatedMeal.getPrice(), result.getPrice());
        assertEquals(updatedMeal.getTime(), result.getTime());
        assertEquals(mealId, result.getId());
        verify(mealRepository, times(1)).findById(mealId);
        verify(mealRepository, times(1)).save(any(Meal.class));
    }

    @Test
    void updateMeal_WithInvalidTime_ShouldThrowException() {
        // Arrange
        Long mealId = 1L;
        Meal updatedMeal = new Meal();
        updatedMeal.setName("Bacalhau à Gomes de Sá");
        updatedMeal.setPrice(16.75);
        updatedMeal.setTime(LocalTime.of(16, 0)); // Horário inválido (entre almoço e jantar)
        updatedMeal.setDate(LocalDate.now());
        updatedMeal.setRestaurant(restaurant);

        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal1));

        // Act & Assert
        Exception exception = assertThrows(InvalidMealTimeException.class, () -> {
            mealService.updateMeal(mealId, updatedMeal);
        });

        assertEquals("O horário deve ser entre 11:00-15:00 para almoço ou 18:00-23:00 para jantar", exception.getMessage());
        verify(mealRepository, times(1)).findById(mealId);
        verify(mealRepository, never()).save(any(Meal.class));
    }

    @Test
    void updateMeal_WithNonChangedTime_ShouldUpdateMeal() {
        // Arrange
        Long mealId = 1L;
        Meal existingMeal = new Meal();
        existingMeal.setId(mealId);
        existingMeal.setName("Bacalhau à Brás");
        existingMeal.setPrice(15.50);
        existingMeal.setTime(LocalTime.of(13, 0));
        existingMeal.setDate(LocalDate.now());
        existingMeal.setRestaurant(restaurant);

        Meal updatedMeal = new Meal();
        updatedMeal.setName("Bacalhau à Gomes de Sá");
        updatedMeal.setPrice(16.75);
        updatedMeal.setTime(null); // Não alterar o horário
        updatedMeal.setDate(LocalDate.now().plusDays(1)); // Nova data
        updatedMeal.setRestaurant(restaurant);

        when(mealRepository.findById(mealId)).thenReturn(Optional.of(existingMeal));
        when(mealRepository.save(any(Meal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Meal result = mealService.updateMeal(mealId, updatedMeal);

        // Assert
        assertNotNull(result);
        assertEquals(updatedMeal.getName(), result.getName());
        assertEquals(updatedMeal.getPrice(), result.getPrice());
        assertEquals(existingMeal.getTime(), result.getTime()); // O tempo deve permanecer o mesmo
        assertEquals(updatedMeal.getDate(), result.getDate());
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
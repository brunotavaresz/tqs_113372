package com.example.bookingMoliceiro.services;

import com.example.bookingMoliceiro.exceptions.RestaurantCapacityExceededException;
import com.example.bookingMoliceiro.models.Meal;
import com.example.bookingMoliceiro.models.Reservation;
import com.example.bookingMoliceiro.models.Restaurant;
import com.example.bookingMoliceiro.repositories.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private MealService mealService;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation testReservation;
    private Restaurant testRestaurant;
    private Meal testMeal;
    private String testReservationCode;

    @BeforeEach
    void setUp() {
        // Configurar restaurante
        testRestaurant = new Restaurant();
        testRestaurant.setId(1L);
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setMaxCapacity(10);
        
        // Configurar refeição
        testMeal = new Meal();
        testMeal.setId(1L);
        testMeal.setName("Test Meal");
        testMeal.setTime(LocalTime.of(13, 0)); // Horário de almoço
        testMeal.setDate(LocalDate.now());
        testMeal.setRestaurant(testRestaurant);
        
        // Configurar reserva de teste
        testReservation = new Reservation();
        testReservationCode = "test-reservation-123";
        testReservation.setId(1L);
        testReservation.setReservationCode(testReservationCode);
        testReservation.setCustomerName("Test Customer");
        testReservation.setRestaurant(testRestaurant);
        testReservation.setMeal(testMeal);
    }

    @Test
    void createReservation_SavesReservation() {
        // Arrange
        when(restaurantService.getRestaurantById(anyLong())).thenReturn(testRestaurant);
        when(reservationRepository.findByRestaurantIdAndMealDate(anyLong(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // Act
        Reservation result = reservationService.createReservation(testReservation);

        // Assert
        assertEquals(testReservation, result);
        verify(reservationRepository).save(testReservation);
    }

    @Test
    void createReservation_ThrowsExceptionWhenRestaurantAtCapacity() {
        // Arrange
        when(restaurantService.getRestaurantById(anyLong())).thenReturn(testRestaurant);
        
        // Criar lista com capacidade máxima de reservas
        List<Reservation> existingReservations = Arrays.asList(
                testReservation, testReservation, testReservation, testReservation, testReservation,
                testReservation, testReservation, testReservation, testReservation, testReservation
        );
        
        when(reservationRepository.findByRestaurantIdAndMealDate(anyLong(), any(LocalDate.class)))
                .thenReturn(existingReservations);

        // Act & Assert
        RestaurantCapacityExceededException exception = assertThrows(RestaurantCapacityExceededException.class, () -> {
            reservationService.createReservation(testReservation);
        });
        
        assertTrue(exception.getMessage().contains("atingiu a capacidade máxima"));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void getAllReservations_ReturnsAllReservations() {
        // Arrange
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findAll()).thenReturn(reservations);

        // Act
        List<Reservation> result = reservationService.getAllReservations();

        // Assert
        assertEquals(1, result.size());
        assertEquals(testReservation, result.get(0));
        verify(reservationRepository).findAll();
    }

    @Test
    void getReservationByCode_ExistingCode_ReturnsReservation() {
        // Arrange
        when(reservationRepository.findByReservationCode(testReservationCode))
            .thenReturn(Optional.of(testReservation));

        // Act
        Reservation result = reservationService.getReservationByCode(testReservationCode);

        // Assert
        assertEquals(testReservation, result);
        verify(reservationRepository).findByReservationCode(testReservationCode);
    }

    @Test
    void getReservationByCode_NonExistingCode_ThrowsException() {
        // Arrange
        String nonExistingCode = "non-existing-code";
        when(reservationRepository.findByReservationCode(nonExistingCode))
            .thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.getReservationByCode(nonExistingCode);
        });
        
        assertEquals("Reserva não encontrada", exception.getMessage());
        verify(reservationRepository).findByReservationCode(nonExistingCode);
    }

    @Test
    void cancelReservation_ExistingReservation_DeletesReservation() {
        // Arrange
        when(reservationRepository.findByReservationCode(testReservationCode))
            .thenReturn(Optional.of(testReservation));

        // Act
        reservationService.cancelReservation(testReservationCode);

        // Assert
        verify(reservationRepository).findByReservationCode(testReservationCode);
        verify(reservationRepository).delete(testReservation);
    }

    @Test
    void checkInReservation_ReservationNotCheckedIn_ChecksInAndReturnsTrue() {
        // Arrange
        testReservation.setCheckedIn(false);
        when(reservationRepository.findByReservationCode(testReservationCode))
            .thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // Act
        boolean result = reservationService.checkInReservation(testReservationCode);

        // Assert
        assertTrue(result);
        assertTrue(testReservation.isCheckedIn());
        verify(reservationRepository).findByReservationCode(testReservationCode);
        verify(reservationRepository).save(testReservation);
    }

    @Test
    void checkInReservation_ReservationAlreadyCheckedIn_ReturnsFalse() {
        // Arrange
        testReservation.setCheckedIn(true);
        when(reservationRepository.findByReservationCode(testReservationCode))
            .thenReturn(Optional.of(testReservation));

        // Act
        boolean result = reservationService.checkInReservation(testReservationCode);

        // Assert
        assertFalse(result);
        verify(reservationRepository).findByReservationCode(testReservationCode);
        verifyNoMoreInteractions(reservationRepository);
    }

    @Test
    void updateReservation_UpdatesCustomerNameOnly() {
        // Arrange
        Reservation updatedReservation = new Reservation();
        updatedReservation.setCustomerName("Updated Customer Name");
        
        when(reservationRepository.findByReservationCode(testReservationCode))
            .thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // Act
        Reservation result = reservationService.updateReservation(testReservationCode, updatedReservation);

        // Assert
        assertEquals(updatedReservation.getCustomerName(), result.getCustomerName());
        verify(reservationRepository).findByReservationCode(testReservationCode);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void updateReservation_UpdatesMealWithNewMeal() {
        // Arrange
        Meal newMeal = new Meal();
        newMeal.setId(2L);
        newMeal.setName("New Test Meal");
        newMeal.setTime(LocalTime.of(19, 0)); // Horário de jantar
        newMeal.setDate(LocalDate.now());
        newMeal.setRestaurant(testRestaurant);
        
        Reservation updatedReservation = new Reservation();
        updatedReservation.setMeal(newMeal);
        
        when(reservationRepository.findByReservationCode(testReservationCode))
            .thenReturn(Optional.of(testReservation));
        when(mealService.getMealById(anyLong())).thenReturn(newMeal);
        when(restaurantService.getRestaurantById(anyLong())).thenReturn(testRestaurant);
        when(reservationRepository.findByRestaurantIdAndMealDate(anyLong(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // Act
        Reservation result = reservationService.updateReservation(testReservationCode, updatedReservation);

        // Assert
        assertEquals(newMeal, result.getMeal());
        verify(reservationRepository).findByReservationCode(testReservationCode);
        verify(mealService).getMealById(newMeal.getId());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void getReservationsByRestaurant_ReturnsReservationsForRestaurant() {
        // Arrange
        Long restaurantId = 1L;
        List<Reservation> expectedReservations = Arrays.asList(testReservation);
        when(reservationRepository.findByRestaurantId(restaurantId)).thenReturn(expectedReservations);

        // Act
        List<Reservation> result = reservationService.getReservationsByRestaurant(restaurantId);

        // Assert
        assertEquals(expectedReservations, result);
        verify(reservationRepository).findByRestaurantId(restaurantId);
    }

    @Test
void createReservation_WithNullRestaurant_ThrowsException() {
    // Arrange
    Reservation invalidReservation = new Reservation();
    invalidReservation.setRestaurant(null);
    invalidReservation.setMeal(testMeal);
    
    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        reservationService.createReservation(invalidReservation);
    });
    
    assertEquals("Restaurante e refeição são obrigatórios para criar uma reserva", exception.getMessage());
    verify(reservationRepository, never()).save(any(Reservation.class));
}

@Test
void createReservation_WithNullMeal_ThrowsException() {
    // Arrange
    Reservation invalidReservation = new Reservation();
    invalidReservation.setRestaurant(testRestaurant);
    invalidReservation.setMeal(null);
    
    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        reservationService.createReservation(invalidReservation);
    });
    
    assertEquals("Restaurante e refeição são obrigatórios para criar uma reserva", exception.getMessage());
    verify(reservationRepository, never()).save(any(Reservation.class));
}

@Test
void createReservation_AtLunchTimeBoundary_Succeeds() {
    // Arrange
    Meal lunchStartMeal = new Meal();
    lunchStartMeal.setId(3L);
    lunchStartMeal.setTime(LocalTime.of(11, 0)); // Start of lunch
    lunchStartMeal.setDate(LocalDate.now());
    lunchStartMeal.setRestaurant(testRestaurant);
    
    Reservation lunchReservation = new Reservation();
    lunchReservation.setReservationCode("lunch-start");
    lunchReservation.setCustomerName("Lunch Customer");
    lunchReservation.setRestaurant(testRestaurant);
    lunchReservation.setMeal(lunchStartMeal);
    
    when(restaurantService.getRestaurantById(anyLong())).thenReturn(testRestaurant);
    when(reservationRepository.findByRestaurantIdAndMealDate(anyLong(), any(LocalDate.class)))
            .thenReturn(Collections.emptyList());
    when(reservationRepository.save(any(Reservation.class))).thenReturn(lunchReservation);

    // Act
    Reservation result = reservationService.createReservation(lunchReservation);

    // Assert
    assertEquals(lunchReservation, result);
    verify(reservationRepository).save(lunchReservation);
}

@Test
void createReservation_AtLunchEndBoundary_Succeeds() {
    // Arrange
    Meal lunchEndMeal = new Meal();
    lunchEndMeal.setId(4L);
    lunchEndMeal.setTime(LocalTime.of(15, 0)); // End of lunch
    lunchEndMeal.setDate(LocalDate.now());
    lunchEndMeal.setRestaurant(testRestaurant);
    
    Reservation lunchReservation = new Reservation();
    lunchReservation.setReservationCode("lunch-end");
    lunchReservation.setCustomerName("Lunch End Customer");
    lunchReservation.setRestaurant(testRestaurant);
    lunchReservation.setMeal(lunchEndMeal);
    
    when(restaurantService.getRestaurantById(anyLong())).thenReturn(testRestaurant);
    when(reservationRepository.findByRestaurantIdAndMealDate(anyLong(), any(LocalDate.class)))
            .thenReturn(Collections.emptyList());
    when(reservationRepository.save(any(Reservation.class))).thenReturn(lunchReservation);

    // Act
    Reservation result = reservationService.createReservation(lunchReservation);

    // Assert
    assertEquals(lunchReservation, result);
    verify(reservationRepository).save(lunchReservation);
}

@Test
void createReservation_AtDinnerTimeBoundary_Succeeds() {
    // Arrange
    Meal dinnerStartMeal = new Meal();
    dinnerStartMeal.setId(5L);
    dinnerStartMeal.setTime(LocalTime.of(18, 0)); // Start of dinner
    dinnerStartMeal.setDate(LocalDate.now());
    dinnerStartMeal.setRestaurant(testRestaurant);
    
    Reservation dinnerReservation = new Reservation();
    dinnerReservation.setReservationCode("dinner-start");
    dinnerReservation.setCustomerName("Dinner Customer");
    dinnerReservation.setRestaurant(testRestaurant);
    dinnerReservation.setMeal(dinnerStartMeal);
    
    when(restaurantService.getRestaurantById(anyLong())).thenReturn(testRestaurant);
    when(reservationRepository.findByRestaurantIdAndMealDate(anyLong(), any(LocalDate.class)))
            .thenReturn(Collections.emptyList());
    when(reservationRepository.save(any(Reservation.class))).thenReturn(dinnerReservation);

    // Act
    Reservation result = reservationService.createReservation(dinnerReservation);

    // Assert
    assertEquals(dinnerReservation, result);
    verify(reservationRepository).save(dinnerReservation);
}

@Test
void createReservation_AtDinnerEndBoundary_Succeeds() {
    // Arrange
    Meal dinnerEndMeal = new Meal();
    dinnerEndMeal.setId(6L);
    dinnerEndMeal.setTime(LocalTime.of(23, 0)); // End of dinner
    dinnerEndMeal.setDate(LocalDate.now());
    dinnerEndMeal.setRestaurant(testRestaurant);
    
    Reservation dinnerReservation = new Reservation();
    dinnerReservation.setReservationCode("dinner-end");
    dinnerReservation.setCustomerName("Dinner End Customer");
    dinnerReservation.setRestaurant(testRestaurant);
    dinnerReservation.setMeal(dinnerEndMeal);
    
    when(restaurantService.getRestaurantById(anyLong())).thenReturn(testRestaurant);
    when(reservationRepository.findByRestaurantIdAndMealDate(anyLong(), any(LocalDate.class)))
            .thenReturn(Collections.emptyList());
    when(reservationRepository.save(any(Reservation.class))).thenReturn(dinnerReservation);

    // Act
    Reservation result = reservationService.createReservation(dinnerReservation);

    // Assert
    assertEquals(dinnerReservation, result);
    verify(reservationRepository).save(dinnerReservation);
}



@Test
void createReservation_OutsideMealPeriods_FiltersCorrectly() {
    // Arrange
    // Create a meal outside normal periods
    Meal oddTimeMeal = new Meal();
    oddTimeMeal.setId(8L);
    oddTimeMeal.setTime(LocalTime.of(16, 30)); // Between lunch and dinner
    oddTimeMeal.setDate(LocalDate.now());
    oddTimeMeal.setRestaurant(testRestaurant);
    
    Reservation oddTimeReservation = new Reservation();
    oddTimeReservation.setReservationCode("odd-time");
    oddTimeReservation.setCustomerName("Odd Time Customer");
    oddTimeReservation.setRestaurant(testRestaurant);
    oddTimeReservation.setMeal(oddTimeMeal);
    
    when(restaurantService.getRestaurantById(anyLong())).thenReturn(testRestaurant);
    when(reservationRepository.findByRestaurantIdAndMealDate(anyLong(), any(LocalDate.class)))
            .thenReturn(Collections.emptyList());
    when(reservationRepository.save(any(Reservation.class))).thenReturn(oddTimeReservation);

    // Act
    Reservation result = reservationService.createReservation(oddTimeReservation);

    // Assert
    assertEquals(oddTimeReservation, result);
    verify(reservationRepository).save(oddTimeReservation);
}

}
package com.example.bookingMoliceiro.services;

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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation testReservation;
    private String testReservationCode;

    @BeforeEach
    void setUp() {
        // Configurar reserva de teste
        testReservation = new Reservation();
        testReservationCode = "test-reservation-123";
        testReservation.setId(1L);
        testReservation.setReservationCode(testReservationCode);
        testReservation.setCustomerName("Test Customer");
        
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        
        Meal meal = new Meal();
        meal.setId(1L);
        meal.setName("Test Meal");
        
        testReservation.setRestaurant(restaurant);
        testReservation.setMeal(meal);
    }

    @Test
    void createReservation_SavesReservation() {
        // Arrange
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // Act
        Reservation result = reservationService.createReservation(testReservation);

        // Assert
        assertEquals(testReservation, result);
        verify(reservationRepository).save(testReservation);
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
    void updateReservation_UpdatesMealAndSavesReservation() {
        // Arrange
        Meal newMeal = new Meal();
        newMeal.setId(2L);
        newMeal.setName("New Test Meal");
        
        Reservation updatedReservation = new Reservation();
        updatedReservation.setId(testReservation.getId());
        updatedReservation.setReservationCode(testReservationCode);
        updatedReservation.setMeal(newMeal);
        
        when(reservationRepository.findByReservationCode(testReservationCode))
            .thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(updatedReservation);

        // Act
        Reservation result = reservationService.updateReservation(testReservationCode, updatedReservation);

        // Assert
        assertEquals(newMeal, result.getMeal());
        verify(reservationRepository).findByReservationCode(testReservationCode);
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
}
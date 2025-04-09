package com.example.bookingMoliceiro.repositories;

import com.example.bookingMoliceiro.models.Meal;
import com.example.bookingMoliceiro.models.Reservation;
import com.example.bookingMoliceiro.models.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ReservationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReservationRepository reservationRepository;
    
    private Restaurant restaurant1;
    private Restaurant restaurant2;
    private Meal meal1;
    private Meal meal2;
    private Reservation reservation1;
    private Reservation reservation2;
    private Reservation reservation3;

    @BeforeEach
    void setUp() {
        // Criar e salvar restaurantes
        restaurant1 = new Restaurant();
        restaurant1.setName("O Bairro");
        restaurant1.setLocation("Aveiro Centro");
        restaurant1.setPhotoUrl("https://example.com/bairro.jpg");
        entityManager.persist(restaurant1);
        
        restaurant2 = new Restaurant();
        restaurant2.setName("Ramona");
        restaurant2.setLocation("Rossio");
        restaurant2.setPhotoUrl("https://example.com/ramona.jpg");
        entityManager.persist(restaurant2);
        
        // Criar e salvar refeições
        meal1 = new Meal();
        meal1.setName("Bacalhau à Brás");
        meal1.setPrice(15.50);
        meal1.setDate(LocalDate.now());
        meal1.setTime(LocalTime.of(12, 30));
        meal1.setRestaurant(restaurant1);
        entityManager.persist(meal1);
        
        meal2 = new Meal();
        meal2.setName("Francesinha");
        meal2.setPrice(12.75);
        meal2.setDate(LocalDate.now());
        meal2.setTime(LocalTime.of(13, 0));
        meal2.setRestaurant(restaurant2);
        entityManager.persist(meal2);
        
        // Criar e salvar reservas
        reservation1 = new Reservation();
        reservation1.setReservationCode("RES123");
        reservation1.setCheckedIn(false);
        reservation1.setRestaurant(restaurant1);
        reservation1.setMeal(meal1);
        reservation1.setCustomerName("João Silva");
        entityManager.persist(reservation1);
        
        reservation2 = new Reservation();
        reservation2.setReservationCode("RES456");
        reservation2.setCheckedIn(true);
        reservation2.setRestaurant(restaurant1);
        reservation2.setMeal(meal1);
        reservation2.setCustomerName("Maria Santos");
        entityManager.persist(reservation2);
        
        reservation3 = new Reservation();
        reservation3.setReservationCode("RES789");
        reservation3.setCheckedIn(false);
        reservation3.setRestaurant(restaurant2);
        reservation3.setMeal(meal2);
        reservation3.setCustomerName("Pedro Costa");
        entityManager.persist(reservation3);
        
        entityManager.flush();
    }
    
    @Test
    void findAll_ShouldReturnAllReservations() {
        // Act
        List<Reservation> reservations = reservationRepository.findAll();
        
        // Assert
        assertEquals(3, reservations.size());
        assertTrue(reservations.stream().anyMatch(r -> r.getReservationCode().equals("RES123")));
        assertTrue(reservations.stream().anyMatch(r -> r.getReservationCode().equals("RES456")));
        assertTrue(reservations.stream().anyMatch(r -> r.getReservationCode().equals("RES789")));
    }
    
    @Test
    void findById_WhenExistingId_ShouldReturnReservation() {
        // Act
        Optional<Reservation> foundReservation = reservationRepository.findById(reservation1.getId());
        
        // Assert
        assertTrue(foundReservation.isPresent());
        assertEquals("RES123", foundReservation.get().getReservationCode());
        assertEquals("João Silva", foundReservation.get().getCustomerName());
    }
    
    @Test
    void findById_WhenNonExistingId_ShouldReturnEmpty() {
        // Act
        Optional<Reservation> foundReservation = reservationRepository.findById(999L);
        
        // Assert
        assertTrue(foundReservation.isEmpty());
    }
    
    @Test
    void findByReservationCode_WhenExistingCode_ShouldReturnReservation() {
        // Act
        Optional<Reservation> foundReservation = reservationRepository.findByReservationCode("RES123");
        
        // Assert
        assertTrue(foundReservation.isPresent());
        assertEquals(reservation1.getId(), foundReservation.get().getId());
        assertEquals("João Silva", foundReservation.get().getCustomerName());
    }
    
    @Test
    void findByReservationCode_WhenNonExistingCode_ShouldReturnEmpty() {
        // Act
        Optional<Reservation> foundReservation = reservationRepository.findByReservationCode("NONEXISTENT");
        
        // Assert
        assertTrue(foundReservation.isEmpty());
    }
    
    @Test
    void findByRestaurantId_ShouldReturnRestaurantReservations() {
        // Act
        List<Reservation> restaurant1Reservations = reservationRepository.findByRestaurantId(restaurant1.getId());
        List<Reservation> restaurant2Reservations = reservationRepository.findByRestaurantId(restaurant2.getId());
        
        // Assert
        assertEquals(2, restaurant1Reservations.size());
        assertTrue(restaurant1Reservations.stream().anyMatch(r -> r.getReservationCode().equals("RES123")));
        assertTrue(restaurant1Reservations.stream().anyMatch(r -> r.getReservationCode().equals("RES456")));
        
        assertEquals(1, restaurant2Reservations.size());
        assertTrue(restaurant2Reservations.stream().anyMatch(r -> r.getReservationCode().equals("RES789")));
    }
    
    @Test
    void findByRestaurantId_WhenNoReservations_ShouldReturnEmptyList() {
        // Arrange
        Restaurant emptyRestaurant = new Restaurant();
        emptyRestaurant.setName("Vazio");
        emptyRestaurant.setLocation("Sem local");
        emptyRestaurant.setPhotoUrl("https://example.com/vazio.jpg");
        entityManager.persist(emptyRestaurant);
        entityManager.flush();
        
        // Act
        List<Reservation> reservations = reservationRepository.findByRestaurantId(emptyRestaurant.getId());
        
        // Assert
        assertTrue(reservations.isEmpty());
    }
    
    @Test
    void save_ShouldSaveReservation() {
        // Arrange
        Reservation newReservation = new Reservation();
        newReservation.setReservationCode("RES999");
        newReservation.setCheckedIn(false);
        newReservation.setRestaurant(restaurant2);
        newReservation.setMeal(meal2);
        newReservation.setCustomerName("Ana Ferreira");
        
        // Act
        Reservation savedReservation = reservationRepository.save(newReservation);
        
        // Assert
        assertNotNull(savedReservation.getId());
        assertEquals("RES999", savedReservation.getReservationCode());
        assertEquals("Ana Ferreira", savedReservation.getCustomerName());
        
        Optional<Reservation> foundReservation = reservationRepository.findById(savedReservation.getId());
        assertTrue(foundReservation.isPresent());
        assertEquals("RES999", foundReservation.get().getReservationCode());
    }
    
    @Test
    void delete_ShouldRemoveReservation() {
        // Act
        reservationRepository.delete(reservation1);
        
        // Assert
        Optional<Reservation> deletedReservation = reservationRepository.findById(reservation1.getId());
        assertTrue(deletedReservation.isEmpty());
        
        Optional<Reservation> deletedByCode = reservationRepository.findByReservationCode("RES123");
        assertTrue(deletedByCode.isEmpty());
        
        List<Reservation> allReservations = reservationRepository.findAll();
        assertEquals(2, allReservations.size());
        assertFalse(allReservations.stream().anyMatch(r -> r.getReservationCode().equals("RES123")));
    }
}
package com.example.bookingMoliceiro.repositories;

import com.example.bookingMoliceiro.models.Meal;
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
public class MealRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MealRepository mealRepository;
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    private Restaurant restaurant1;
    private Restaurant restaurant2;
    private Meal meal1;
    private Meal meal2;
    private Meal meal3;

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
        meal2.setRestaurant(restaurant1);
        entityManager.persist(meal2);
        
        meal3 = new Meal();
        meal3.setName("Polvo à Lagareiro");
        meal3.setPrice(18.90);
        meal3.setDate(LocalDate.now().plusDays(1));
        meal3.setTime(LocalTime.of(19, 30));
        meal3.setRestaurant(restaurant2);
        entityManager.persist(meal3);
        
        entityManager.flush();
    }
    
    @Test
    void findAll_ShouldReturnAllMeals() {
        // Act
        List<Meal> meals = mealRepository.findAll();
        
        // Assert
        assertEquals(3, meals.size());
        assertTrue(meals.stream().anyMatch(m -> m.getName().equals("Bacalhau à Brás")));
        assertTrue(meals.stream().anyMatch(m -> m.getName().equals("Francesinha")));
        assertTrue(meals.stream().anyMatch(m -> m.getName().equals("Polvo à Lagareiro")));
    }
    
    @Test
    void findById_WhenExistingId_ShouldReturnMeal() {
        // Act
        Optional<Meal> foundMeal = mealRepository.findById(meal1.getId());
        
        // Assert
        assertTrue(foundMeal.isPresent());
        assertEquals("Bacalhau à Brás", foundMeal.get().getName());
        assertEquals(15.50, foundMeal.get().getPrice());
    }
    
    @Test
    void findById_WhenNonExistingId_ShouldReturnEmpty() {
        // Act
        Optional<Meal> foundMeal = mealRepository.findById(999L);
        
        // Assert
        assertTrue(foundMeal.isEmpty());
    }
    
    @Test
    void save_ShouldSaveMeal() {
        // Arrange
        Meal newMeal = new Meal();
        newMeal.setName("Arroz de Marisco");
        newMeal.setPrice(20.50);
        newMeal.setDate(LocalDate.now().plusDays(2));
        newMeal.setTime(LocalTime.of(20, 0));
        newMeal.setRestaurant(restaurant2);
        
        // Act
        Meal savedMeal = mealRepository.save(newMeal);
        
        // Assert
        assertNotNull(savedMeal.getId());
        assertEquals("Arroz de Marisco", savedMeal.getName());
        assertEquals(20.50, savedMeal.getPrice());
        
        Optional<Meal> foundMeal = mealRepository.findById(savedMeal.getId());
        assertTrue(foundMeal.isPresent());
        assertEquals("Arroz de Marisco", foundMeal.get().getName());
    }
    
    @Test
    void findByRestaurantId_ShouldReturnRestaurantMeals() {
        // Act
        List<Meal> restaurant1Meals = mealRepository.findByRestaurantId(restaurant1.getId());
        List<Meal> restaurant2Meals = mealRepository.findByRestaurantId(restaurant2.getId());
        
        // Assert
        assertEquals(2, restaurant1Meals.size());
        assertTrue(restaurant1Meals.stream().anyMatch(m -> m.getName().equals("Bacalhau à Brás")));
        assertTrue(restaurant1Meals.stream().anyMatch(m -> m.getName().equals("Francesinha")));
        
        assertEquals(1, restaurant2Meals.size());
        assertTrue(restaurant2Meals.stream().anyMatch(m -> m.getName().equals("Polvo à Lagareiro")));
    }
    
    @Test
    void findByRestaurantId_WhenNoMeals_ShouldReturnEmptyList() {
        // Arrange
        Restaurant emptyRestaurant = new Restaurant();
        emptyRestaurant.setName("Vazio");
        emptyRestaurant.setLocation("Sem local");
        emptyRestaurant.setPhotoUrl("https://example.com/vazio.jpg");
        entityManager.persist(emptyRestaurant);
        entityManager.flush();
        
        // Act
        List<Meal> meals = mealRepository.findByRestaurantId(emptyRestaurant.getId());
        
        // Assert
        assertTrue(meals.isEmpty());
    }
    
    @Test
    void delete_ShouldRemoveMeal() {
        // Act
        mealRepository.delete(meal1);
        
        // Assert
        Optional<Meal> deletedMeal = mealRepository.findById(meal1.getId());
        assertTrue(deletedMeal.isEmpty());
        
        List<Meal> allMeals = mealRepository.findAll();
        assertEquals(2, allMeals.size());
    }
}
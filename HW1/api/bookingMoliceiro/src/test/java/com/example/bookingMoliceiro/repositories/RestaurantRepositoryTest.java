package com.example.bookingMoliceiro.repositories;

import com.example.bookingMoliceiro.models.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RestaurantRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RestaurantRepository restaurantRepository;
    
    private Restaurant restaurant1;
    private Restaurant restaurant2;

    @BeforeEach
    void setUp() {
        // Criar e salvar restaurantes
        restaurant1 = new Restaurant();
        restaurant1.setName("O Bairro");
        restaurant1.setLocation("Aveiro Centro");
        entityManager.persist(restaurant1);
        
        restaurant2 = new Restaurant();
        restaurant2.setName("Ramona");
        restaurant2.setLocation("Rossio");
        entityManager.persist(restaurant2);
        
        entityManager.flush();
    }
    
    @Test
    void findAll_ShouldReturnAllRestaurants() {
        // Act
        List<Restaurant> restaurants = restaurantRepository.findAll();
        
        // Assert
        assertEquals(2, restaurants.size());
        assertTrue(restaurants.stream().anyMatch(r -> r.getName().equals("O Bairro")));
        assertTrue(restaurants.stream().anyMatch(r -> r.getName().equals("Ramona")));
    }
    
    @Test
    void findById_WhenExistingId_ShouldReturnRestaurant() {
        // Act
        Optional<Restaurant> foundRestaurant = restaurantRepository.findById(restaurant1.getId());
        
        // Assert
        assertTrue(foundRestaurant.isPresent());
        assertEquals("O Bairro", foundRestaurant.get().getName());
        assertEquals("Aveiro Centro", foundRestaurant.get().getLocation());
    }
    
    @Test
    void findById_WhenNonExistingId_ShouldReturnEmpty() {
        // Act
        Optional<Restaurant> foundRestaurant = restaurantRepository.findById(999L);
        
        // Assert
        assertTrue(foundRestaurant.isEmpty());
    }
    
    @Test
    void save_ShouldSaveRestaurant() {
        // Arrange
        Restaurant newRestaurant = new Restaurant();
        newRestaurant.setName("Salpoente");
        newRestaurant.setLocation("Canal de São Roque");
        
        // Act
        Restaurant savedRestaurant = restaurantRepository.save(newRestaurant);
        
        // Assert
        assertNotNull(savedRestaurant.getId());
        assertEquals("Salpoente", savedRestaurant.getName());
        assertEquals("Canal de São Roque", savedRestaurant.getLocation());
        
        Optional<Restaurant> foundRestaurant = restaurantRepository.findById(savedRestaurant.getId());
        assertTrue(foundRestaurant.isPresent());
        assertEquals("Salpoente", foundRestaurant.get().getName());
    }
    
    @Test
    void update_ShouldUpdateRestaurant() {
        // Act
        restaurant1.setName("O Bairro Novo");
        restaurant1.setLocation("Praça do Peixe");
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant1);
        
        // Assert
        assertEquals(restaurant1.getId(), updatedRestaurant.getId());
        assertEquals("O Bairro Novo", updatedRestaurant.getName());
        assertEquals("Praça do Peixe", updatedRestaurant.getLocation());
        
        Optional<Restaurant> foundRestaurant = restaurantRepository.findById(restaurant1.getId());
        assertTrue(foundRestaurant.isPresent());
        assertEquals("O Bairro Novo", foundRestaurant.get().getName());
        assertEquals("Praça do Peixe", foundRestaurant.get().getLocation());
    }
    
    @Test
    void delete_ShouldRemoveRestaurant() {
        // Act
        restaurantRepository.delete(restaurant1);
        
        // Assert
        Optional<Restaurant> deletedRestaurant = restaurantRepository.findById(restaurant1.getId());
        assertTrue(deletedRestaurant.isEmpty());
        
        List<Restaurant> allRestaurants = restaurantRepository.findAll();
        assertEquals(1, allRestaurants.size());
        assertEquals("Ramona", allRestaurants.get(0).getName());
    }
}
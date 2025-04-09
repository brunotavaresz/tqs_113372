package com.example.bookingMoliceiro.services;

import com.example.bookingMoliceiro.models.Restaurant;
import com.example.bookingMoliceiro.repositories.RestaurantRepository;
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
public class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant restaurant1;
    private Restaurant restaurant2;
    private List<Restaurant> restaurantList;

    @BeforeEach
    void setUp() {
        restaurant1 = new Restaurant();
        restaurant1.setId(1L);
        restaurant1.setName("O Bairro");
        restaurant1.setLocation("Aveiro Centro");
        restaurant1.setMaxCapacity(50);

        restaurant2 = new Restaurant();
        restaurant2.setId(2L);
        restaurant2.setName("Ramona");
        restaurant2.setLocation("Rossio");
        restaurant2.setMaxCapacity(30);

        restaurantList = Arrays.asList(restaurant1, restaurant2);
    }

    @Test
    void getAllRestaurants_ShouldReturnAllRestaurants() {
        // Arrange
        when(restaurantRepository.findAll()).thenReturn(restaurantList);

        // Act
        List<Restaurant> result = restaurantService.getAllRestaurants();

        // Assert
        assertEquals(2, result.size());
        assertEquals(restaurant1, result.get(0));
        assertEquals(restaurant2, result.get(1));
        verify(restaurantRepository, times(1)).findAll();
    }

    @Test
    void saveRestaurant_ShouldSaveRestaurant_WithValidData() {
        // Arrange
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant1);

        // Act
        Restaurant savedRestaurant = restaurantService.saveRestaurant(restaurant1);

        // Assert
        assertNotNull(savedRestaurant);
        assertEquals(restaurant1.getName(), savedRestaurant.getName());
        assertEquals(restaurant1.getMaxCapacity(), savedRestaurant.getMaxCapacity());
        verify(restaurantRepository, times(1)).save(restaurant1);
    }

    @Test
    void saveRestaurant_ShouldSaveRestaurant_WithInvalidMaxCapacity() {
        // Arrange
        restaurant1.setMaxCapacity(0); // Capacidade inválida, deve ser ajustada para 20
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant1);

        // Act
        Restaurant savedRestaurant = restaurantService.saveRestaurant(restaurant1);

        // Assert
        assertNotNull(savedRestaurant);
        assertEquals(20, savedRestaurant.getMaxCapacity()); // Verifique se a capacidade foi ajustada para 20
        verify(restaurantRepository, times(1)).save(restaurant1);
    }

    @Test
    void getRestaurantById_WhenExistingId_ShouldReturnRestaurant() {
        // Arrange
        Long restaurantId = 1L;
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant1));

        // Act
        Restaurant foundRestaurant = restaurantService.getRestaurantById(restaurantId);

        // Assert
        assertNotNull(foundRestaurant);
        assertEquals(restaurantId, foundRestaurant.getId());
        assertEquals(restaurant1.getName(), foundRestaurant.getName());
        verify(restaurantRepository, times(1)).findById(restaurantId);
    }

    @Test
    void getRestaurantById_WhenNonExistingId_ShouldThrowException() {
        // Arrange
        Long restaurantId = 99L;
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            restaurantService.getRestaurantById(restaurantId);
        });

        assertEquals("Restaurante não encontrado", exception.getMessage());
        verify(restaurantRepository, times(1)).findById(restaurantId);
    }

    @Test
    void updateRestaurant_WhenExistingId_ShouldUpdateRestaurant() {
        // Arrange
        Long restaurantId = 1L;
        Restaurant updatedRestaurant = new Restaurant();
        updatedRestaurant.setName("O Bairro Novo");
        updatedRestaurant.setLocation("Praça do Peixe");
        updatedRestaurant.setMaxCapacity(60);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant1));
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Restaurant result = restaurantService.updateRestaurant(restaurantId, updatedRestaurant);

        // Assert
        assertNotNull(result);
        assertEquals(updatedRestaurant.getName(), result.getName());
        assertEquals(updatedRestaurant.getLocation(), result.getLocation());
        assertEquals(updatedRestaurant.getMaxCapacity(), result.getMaxCapacity());
        assertEquals(restaurantId, result.getId());
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }
}

package com.example.bookingMoliceiro.services;

import com.example.bookingMoliceiro.models.Restaurant;
import com.example.bookingMoliceiro.repositories.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantService.class);  // Logger criado

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<Restaurant> getAllRestaurants() {
        logger.info("Fetching all restaurants.");
        return restaurantRepository.findAll();
    }

    public Restaurant saveRestaurant(Restaurant restaurant) {
        logger.info("Saving restaurant: {}", restaurant.getName());
        return restaurantRepository.save(restaurant);
    }

    public Restaurant getRestaurantById(Long restaurantId) {
        logger.info("Fetching restaurant with ID: {}", restaurantId);
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> {
                    logger.warn("Restaurant with ID {} not found", restaurantId);
                    return new IllegalArgumentException("Restaurante não encontrado");
                });
    }

    public Restaurant updateRestaurant(Long restaurantId, Restaurant restaurant) {
        logger.info("Updating restaurant with ID: {}", restaurantId);
        Restaurant existingRestaurant = getRestaurantById(restaurantId);
        existingRestaurant.setName(restaurant.getName());
        existingRestaurant.setLocation(restaurant.getLocation());
        return restaurantRepository.save(existingRestaurant);
    }
}

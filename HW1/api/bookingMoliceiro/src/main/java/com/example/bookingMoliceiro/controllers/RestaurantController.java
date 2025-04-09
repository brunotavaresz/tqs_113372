package com.example.bookingMoliceiro.controllers;

import com.example.bookingMoliceiro.models.Meal;
import com.example.bookingMoliceiro.models.Restaurant;
import com.example.bookingMoliceiro.models.Reservation;
import com.example.bookingMoliceiro.services.MealService;
import com.example.bookingMoliceiro.services.RestaurantService;
import com.example.bookingMoliceiro.services.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);
    private final RestaurantService restaurantService;
    private final MealService mealService;
    private final ReservationService reservationService;

    public RestaurantController(RestaurantService restaurantService, MealService mealService, ReservationService reservationService) {
        this.restaurantService = restaurantService;
        this.mealService = mealService;
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<Restaurant> getAllRestaurants() {
        logger.info("Fetching all restaurants.");
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        logger.info("Found {} restaurants.", restaurants.size());
        return restaurants;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurant createRestaurant(@RequestBody Restaurant restaurant) {
        logger.info("Creating restaurant: {}", restaurant.getName());
        Restaurant createdRestaurant = restaurantService.saveRestaurant(restaurant);
        logger.info("Restaurant created with ID: {}", createdRestaurant.getId());
        return createdRestaurant;
    }

    @GetMapping("/{restaurantId}")
    public Restaurant getRestaurantById(@PathVariable Long restaurantId) {
        logger.info("Fetching restaurant with ID: {}", restaurantId);
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        logger.info("Restaurant found: {}", restaurant.getName());
        return restaurant;
    }

    @PutMapping("/{restaurantId}")
    public Restaurant updateRestaurant(@PathVariable Long restaurantId, @RequestBody Restaurant restaurant) {
        logger.info("Updating restaurant with ID: {}", restaurantId);
        Restaurant updatedRestaurant = restaurantService.updateRestaurant(restaurantId, restaurant);
        logger.info("Restaurant updated: {}", updatedRestaurant.getName());
        return updatedRestaurant;
    }

    @GetMapping("/{restaurantId}/menus")
    public List<Meal> getMenusByRestaurant(@PathVariable Long restaurantId) {
        logger.info("Fetching menus for restaurant with ID: {}", restaurantId);
        List<Meal> meals = mealService.getMenusByRestaurant(restaurantId);
        logger.info("Found {} meals for restaurant ID: {}", meals.size(), restaurantId);
        return meals;
    }

    @GetMapping("/{restaurantId}/reservations")
    public List<Reservation> getReservationsByRestaurant(@PathVariable Long restaurantId) {
        logger.info("Fetching reservations for restaurant with ID: {}", restaurantId);
        List<Reservation> reservations = reservationService.getReservationsByRestaurant(restaurantId);
        logger.info("Found {} reservations for restaurant ID: {}", reservations.size(), restaurantId);
        return reservations;
    }
}

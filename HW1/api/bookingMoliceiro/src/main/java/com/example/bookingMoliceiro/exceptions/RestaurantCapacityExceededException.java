package com.example.bookingMoliceiro.exceptions;

public class RestaurantCapacityExceededException extends RuntimeException {
    public RestaurantCapacityExceededException(String message) {
        super(message);
    }
}
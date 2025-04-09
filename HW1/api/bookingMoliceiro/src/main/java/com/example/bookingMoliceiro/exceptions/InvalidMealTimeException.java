package com.example.bookingMoliceiro.exceptions;

public class InvalidMealTimeException extends RuntimeException {
    public InvalidMealTimeException(String message) {
        super(message);
    }
}
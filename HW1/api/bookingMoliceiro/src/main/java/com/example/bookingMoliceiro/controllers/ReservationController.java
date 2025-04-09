package com.example.bookingMoliceiro.controllers;

import com.example.bookingMoliceiro.exceptions.RestaurantCapacityExceededException;
import com.example.bookingMoliceiro.models.Reservation;
import com.example.bookingMoliceiro.services.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody Reservation reservation) {
        try {
            logger.info("Attempting to create a reservation: {}", reservation);
            Reservation createdReservation = reservationService.createReservation(reservation);
            logger.info("Reservation created successfully with ID: {}", createdReservation.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
        } catch (RestaurantCapacityExceededException e) {
            logger.warn("Failed to create reservation: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            logger.error("Error creating reservation: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao criar reserva: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        logger.info("Fetching all reservations.");
        List<Reservation> reservations = reservationService.getAllReservations();
        logger.info("Fetched {} reservations.", reservations.size());
        return reservations;
    }

    @GetMapping("/{reservationCode}")
    public ResponseEntity<?> getReservationByCode(@PathVariable String reservationCode) {
        try {
            logger.info("Fetching reservation with code: {}", reservationCode);
            Reservation reservation = reservationService.getReservationByCode(reservationCode);
            logger.info("Found reservation: {}", reservation);
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            logger.error("Error fetching reservation: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @DeleteMapping("/{reservationCode}")
    public ResponseEntity<?> cancelReservation(@PathVariable String reservationCode) {
        try {
            logger.info("Attempting to cancel reservation with code: {}", reservationCode);
            reservationService.cancelReservation(reservationCode);
            logger.info("Reservation with code {} has been successfully canceled.", reservationCode);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Reserva cancelada com sucesso");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error canceling reservation with code {}: {}", reservationCode, e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping("/{reservationCode}/check-in")
    public ResponseEntity<?> checkIn(@PathVariable String reservationCode) {
        try {
            logger.info("Attempting to check in reservation with code: {}", reservationCode);
            boolean success = reservationService.checkInReservation(reservationCode);
            if (success) {
                logger.info("Check-in successful for reservation with code: {}", reservationCode);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Check-in realizado com sucesso");
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Failed to check in reservation with code: {}", reservationCode);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Reserva já possui check-in");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Error during check-in: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PutMapping("/{reservationCode}")
    public ResponseEntity<?> updateReservation(@PathVariable String reservationCode, @RequestBody Reservation reservation) {
        try {
            logger.info("Attempting to update reservation with code: {}", reservationCode);
            Reservation updatedReservation = reservationService.updateReservation(reservationCode, reservation);
            logger.info("Reservation with code {} updated successfully: {}", reservationCode, updatedReservation);
            return ResponseEntity.ok(updatedReservation);
        } catch (RestaurantCapacityExceededException e) {
            logger.warn("Failed to update reservation: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            logger.error("Error updating reservation: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Handler para exceções de capacidade
    @ExceptionHandler(RestaurantCapacityExceededException.class)
    public ResponseEntity<Map<String, String>> handleCapacityExceededException(RestaurantCapacityExceededException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
package com.example.bookingMoliceiro.controllers;

import com.example.bookingMoliceiro.models.Reservation;
import com.example.bookingMoliceiro.services.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        logger.info("Attempting to create a reservation: {}", reservation);
        Reservation createdReservation = reservationService.createReservation(reservation);
        logger.info("Reservation created successfully with ID: {}", createdReservation.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);  // Retorna status 201
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        logger.info("Fetching all reservations.");
        List<Reservation> reservations = reservationService.getAllReservations();
        logger.info("Fetched {} reservations.", reservations.size());
        return reservations;
    }

    @GetMapping("/{reservationCode}")
    public Reservation getReservationByCode(@PathVariable String reservationCode) {
        logger.info("Fetching reservation with code: {}", reservationCode);
        Reservation reservation = reservationService.getReservationByCode(reservationCode);
        logger.info("Found reservation: {}", reservation);
        return reservation;
    }

    @DeleteMapping("/{reservationCode}")
    public void cancelReservation(@PathVariable String reservationCode) {
        logger.info("Attempting to cancel reservation with code: {}", reservationCode);
        try {
            reservationService.cancelReservation(reservationCode);
            logger.info("Reservation with code {} has been successfully canceled.", reservationCode);
        } catch (Exception e) {
            logger.error("Error canceling reservation with code {}: {}", reservationCode, e.getMessage());
            throw e;  // Re-throw exception after logging
        }
    }

    @PostMapping("/{reservationCode}/check-in")
    public String checkIn(@PathVariable String reservationCode) {
        logger.info("Attempting to check in reservation with code: {}", reservationCode);
        boolean success = reservationService.checkInReservation(reservationCode);
        if (success) {
            logger.info("Check-in successful for reservation with code: {}", reservationCode);
            return "Reserva realizada com sucesso.";
        } else {
            logger.warn("Failed to check in reservation with code: {}", reservationCode);
            return "Falha ao realizar check-in.";
        }
    }

    @PutMapping("/{reservationCode}")
    public Reservation updateReservation(@PathVariable String reservationCode, @RequestBody Reservation reservation) {
        logger.info("Attempting to update reservation with code: {}", reservationCode);
        Reservation updatedReservation = reservationService.updateReservation(reservationCode, reservation);
        logger.info("Reservation with code {} updated successfully: {}", reservationCode, updatedReservation);
        return updatedReservation;
    }
}

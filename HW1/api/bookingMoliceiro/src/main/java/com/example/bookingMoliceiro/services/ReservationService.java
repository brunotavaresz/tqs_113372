package com.example.bookingMoliceiro.services;

import com.example.bookingMoliceiro.models.Reservation;
import com.example.bookingMoliceiro.repositories.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);  // Logger criado

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation createReservation(Reservation reservation) {
        logger.info("Creating reservation with code: {}", reservation.getReservationCode());
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        logger.info("Fetching all reservations.");
        return reservationRepository.findAll();
    }

    public Reservation getReservationByCode(String reservationCode) {
        logger.info("Fetching reservation with code: {}", reservationCode);
        return reservationRepository.findByReservationCode(reservationCode)
                .orElseThrow(() -> {
                    logger.warn("Reservation with code {} not found", reservationCode);
                    return new IllegalArgumentException("Reserva não encontrada");
                });
    }

    public void cancelReservation(String reservationCode) {
        logger.info("Cancelling reservation with code: {}", reservationCode);
        Reservation reservation = getReservationByCode(reservationCode);
        reservationRepository.delete(reservation);
    }

    public boolean checkInReservation(String reservationCode) {
        logger.info("Checking in reservation with code: {}", reservationCode);
        Reservation reservation = getReservationByCode(reservationCode);
        if (reservation.isCheckedIn()) {
            logger.warn("Reservation with code {} is already checked in", reservationCode);
            return false;
        }
        reservation.setCheckedIn(true);
        reservationRepository.save(reservation);
        return true;
    }

    public Reservation updateReservation(String reservationCode, Reservation reservation) {
        logger.info("Updating reservation with code: {}", reservationCode);
        Reservation existingReservation = getReservationByCode(reservationCode);
        existingReservation.setMeal(reservation.getMeal());
        return reservationRepository.save(existingReservation);
    }

    public List<Reservation> getReservationsByRestaurant(Long restaurantId) {
        logger.info("Fetching reservations for restaurant with ID: {}", restaurantId);
        return reservationRepository.findByRestaurantId(restaurantId);
    }
}

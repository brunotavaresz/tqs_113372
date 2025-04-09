package com.example.bookingMoliceiro.repositories;

import com.example.bookingMoliceiro.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReservationCode(String reservationCode);
    List<Reservation> findByRestaurantId(Long restaurantId);
}

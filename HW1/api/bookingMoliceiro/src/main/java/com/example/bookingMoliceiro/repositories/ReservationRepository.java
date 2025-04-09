package com.example.bookingMoliceiro.repositories;

import com.example.bookingMoliceiro.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReservationCode(String reservationCode);
    
    List<Reservation> findByRestaurantId(Long restaurantId);
    
    @Query("SELECT r FROM Reservation r WHERE r.restaurant.id = :restaurantId AND r.meal.date = :date")
    List<Reservation> findByRestaurantIdAndMealDate(
            @Param("restaurantId") Long restaurantId, 
            @Param("date") LocalDate date);
}
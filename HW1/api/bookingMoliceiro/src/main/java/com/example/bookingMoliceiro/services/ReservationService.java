package com.example.bookingMoliceiro.services;

import com.example.bookingMoliceiro.exceptions.RestaurantCapacityExceededException;
import com.example.bookingMoliceiro.models.Meal;
import com.example.bookingMoliceiro.models.Reservation;
import com.example.bookingMoliceiro.models.Restaurant;
import com.example.bookingMoliceiro.repositories.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepository reservationRepository;
    private final RestaurantService restaurantService;
    private final MealService mealService;

    // Definir os horários de almoço e jantar
    private static final LocalTime ALMOCO_INICIO = LocalTime.of(11, 0);
    private static final LocalTime ALMOCO_FIM = LocalTime.of(15, 0);
    private static final LocalTime JANTAR_INICIO = LocalTime.of(18, 0);
    private static final LocalTime JANTAR_FIM = LocalTime.of(23, 0);

    public ReservationService(ReservationRepository reservationRepository, 
                              RestaurantService restaurantService,
                              MealService mealService) {
        this.reservationRepository = reservationRepository;
        this.restaurantService = restaurantService;
        this.mealService = mealService;
    }

    public Reservation createReservation(Reservation reservation) {
        logger.info("Creating reservation with code: {}", reservation.getReservationCode());
        
        // Obter o restaurante e a refeição
        Restaurant restaurant = reservation.getRestaurant();
        Meal meal = reservation.getMeal();
        
        if (restaurant == null || meal == null) {
            logger.error("Restaurant or meal is null in reservation");
            throw new IllegalArgumentException("Restaurante e refeição são obrigatórios para criar uma reserva");
        }
        
        // Verificar a capacidade do restaurante para o período da refeição
        checkRestaurantCapacity(restaurant.getId(), meal.getTime(), meal.getDate());
        
        return reservationRepository.save(reservation);
    }

    private void checkRestaurantCapacity(Long restaurantId, LocalTime mealTime, LocalDate mealDate) {
        // Obter o restaurante para verificar sua capacidade máxima
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        
        // Determinar se é almoço ou jantar
        boolean isLunchTime = !mealTime.isBefore(ALMOCO_INICIO) && !mealTime.isAfter(ALMOCO_FIM);
        boolean isDinnerTime = !mealTime.isBefore(JANTAR_INICIO) && !mealTime.isAfter(JANTAR_FIM);
        
        String mealPeriod = isLunchTime ? "almoço" : (isDinnerTime ? "jantar" : "refeição");
        
        // Contar quantas reservas já existem para este restaurante, data e período
        List<Reservation> existingReservations = reservationRepository.findByRestaurantIdAndMealDate(restaurantId, mealDate);
        
        // Filtrar apenas as reservas para o mesmo período (almoço ou jantar)
        long reservationsCount = existingReservations.stream()
            .filter(reservation -> {
                LocalTime reservationTime = reservation.getMeal().getTime();
                if (isLunchTime) {
                    return !reservationTime.isBefore(ALMOCO_INICIO) && !reservationTime.isAfter(ALMOCO_FIM);
                } else if (isDinnerTime) {
                    return !reservationTime.isBefore(JANTAR_INICIO) && !reservationTime.isAfter(JANTAR_FIM);
                }
                return false;
            })
            .count();
        
        // Verificar se ultrapassou a capacidade máxima
        if (reservationsCount >= restaurant.getMaxCapacity()) {
            logger.warn("Restaurant {} reached maximum capacity for {} on {}", 
                    restaurant.getName(), mealPeriod, mealDate);
            throw new RestaurantCapacityExceededException(
                    String.format("O restaurante %s atingiu a capacidade máxima para %s na data %s", 
                            restaurant.getName(), mealPeriod, mealDate));
        }
        
        logger.info("Restaurant {} has capacity for {} on {} ({}/{} reservations)", 
                restaurant.getName(), mealPeriod, mealDate, reservationsCount, restaurant.getMaxCapacity());
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
        
        // Se estiver alterando a refeição, verificar capacidade
        if (reservation.getMeal() != null && 
                !reservation.getMeal().getId().equals(existingReservation.getMeal().getId())) {
            Meal newMeal = mealService.getMealById(reservation.getMeal().getId());
            checkRestaurantCapacity(
                    existingReservation.getRestaurant().getId(), 
                    newMeal.getTime(), 
                    newMeal.getDate());
            existingReservation.setMeal(newMeal);
        }
        
        // Outras atualizações necessárias
        if (reservation.getCustomerName() != null) {
            existingReservation.setCustomerName(reservation.getCustomerName());
        }
        
        return reservationRepository.save(existingReservation);
    }

    public List<Reservation> getReservationsByRestaurant(Long restaurantId) {
        logger.info("Fetching reservations for restaurant with ID: {}", restaurantId);
        return reservationRepository.findByRestaurantId(restaurantId);
    }
}
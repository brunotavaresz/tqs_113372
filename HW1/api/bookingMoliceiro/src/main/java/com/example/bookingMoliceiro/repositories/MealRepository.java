package com.example.bookingMoliceiro.repositories;
import com.example.bookingMoliceiro.models.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;
@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
List<Meal> findByRestaurantId(Long restaurantId);
}
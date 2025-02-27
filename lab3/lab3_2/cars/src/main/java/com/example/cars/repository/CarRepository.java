package com.example.cars.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cars.object.Car;

public interface CarRepository extends JpaRepository<Car, Long> {

    public Car findByCarId(Long carId);
    
    public List<Car> findAll();
}
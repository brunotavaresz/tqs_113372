package com.example.cars.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cars.object.Car;
import com.example.cars.repository.CarRepository;

@Service
public class CarManagerService {
    @Autowired
    private CarRepository carRepository;

    public Car save(Car car) {
        return carRepository.save(car);
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCarDetails(Long id) {
        return carRepository.findByCarId(id);
    }

    public Car findReplacementCar(Car originalCar) {
        Optional<Car> replacementCar = carRepository.findAll().stream()
            .filter(car -> car.getCarId() != null && !car.getCarId().equals(originalCar.getCarId()) // Carro com ID diferente
                    && car.getSegment().equals(originalCar.getSegment())    // Mesmo segmento
                    && car.getEngineType().equals(originalCar.getEngineType())) // Mesmo tipo de motor
            .findFirst(); // Pega o primeiro que atender as condições
    
        return replacementCar.orElse(null); // Retorna null se não encontrar nenhum carro substituto
    }
    
    
    
    
    
    
}
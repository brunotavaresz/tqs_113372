package com.example.cars.controller;

import com.example.cars.object.Car;
import com.example.cars.service.CarManagerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarsController {

    private final CarManagerService carService;

    public CarsController(CarManagerService carService) {
        this.carService = carService;
    }

    // Criar um carro
    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        Car newCar = carService.save(car);
        return new ResponseEntity<>(newCar, HttpStatus.CREATED);
    }

    // Buscar todos os carros
    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    // Buscar um carro por ID
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        Car car = carService.getCarDetails(id);
        return car != null ? ResponseEntity.ok(car) : ResponseEntity.notFound().build();
    }
}

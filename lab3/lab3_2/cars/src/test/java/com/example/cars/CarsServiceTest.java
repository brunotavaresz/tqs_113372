package com.example.cars;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.assertj.core.api.Assertions.assertThat;


import com.example.cars.controller.CarsController;
import com.example.cars.object.Car;
import com.example.cars.repository.CarRepository;
import com.example.cars.service.CarManagerService;

@ExtendWith(MockitoExtension.class)
public class CarsServiceTest {
    
    @Mock(lenient = true)
    private CarRepository carRepository;

    @InjectMocks
    private CarManagerService carService;

    @BeforeEach
    public void setUp() {
        // Criando carros mockados com segmentos e tipos de motor
        Car car1 = new Car("Mercedes", "SLK200");
        car1.setCarId(1L);
        car1.setSegment("Sedan");
        car1.setEngineType("Gasoline");

        Car car2 = new Car("BMW", "320i");
        car2.setCarId(2L);
        car2.setSegment("Sedan");
        car2.setEngineType("Gasoline");

        Car car3 = new Car("Toyota", "Hilux");
        car3.setCarId(3L);
        car3.setSegment("SUV");
        car3.setEngineType("Diesel");

        List<Car> allCars = List.of(car1, car2, car3);

        // Mockando o repositório para retornar os carros
        when(carRepository.findAll()).thenReturn(allCars);
        when(carRepository.findByCarId(1L)).thenReturn(car1);
        when(carRepository.findByCarId(2L)).thenReturn(car2);
        when(carRepository.findByCarId(3L)).thenReturn(car3);
    }

    @Test
    public void testFindReplacementCar() {
        // Criando um carro de exemplo (Mercedes)
        Car originalCar = new Car("Mercedes", "SLK200");
        originalCar.setCarId(1L);
        originalCar.setSegment("Sedan");
        originalCar.setEngineType("Gasoline");

        // Chama o método para encontrar o carro substituto
        Car replacementCar = carService.findReplacementCar(originalCar);

        // Verificando se o carro substituto está correto
        assertThat(replacementCar).isNotNull();
        assertThat(replacementCar.getMake()).isEqualTo("BMW");
        assertThat(replacementCar.getModel()).isEqualTo("320i");
    }

    @Test
    public void testCarDetailsValid() {
        Car carToBeFound = carService.getCarDetails(1L);

        assertThat(carToBeFound.getMake()).isEqualTo("Mercedes");
    }

    @Test
    public void testGetCarDetailsWrong() {
        Car carToBeFound = carService.getCarDetails(54L);

        assertThat(carToBeFound).isNull();
    }

    @Test
    public void testGetAllCars() {
        List<Car> cars = carService.getAllCars();

        assertThat(cars).hasSize(3).extracting(Car::getMake).contains("Mercedes", "BMW", "Toyota");
        verify(carRepository).findAll();
    }
}

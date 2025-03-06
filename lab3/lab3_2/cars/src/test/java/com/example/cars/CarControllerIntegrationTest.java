package com.example.cars;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import com.example.cars.controller.CarsController;
import com.example.cars.object.Car;
import com.example.cars.repository.CarRepository;
import com.example.cars.service.CarManagerService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CarControllerIntegrationTest {

    @LocalServerPort
    private int randomServerPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CarRepository carRepository;

    @AfterEach
    void clearDatabase() {
        carRepository.deleteAll();
    }

    @Test
    void shouldCreateCar() {
        // Criando um carro para ser salvo
        Car car = new Car("Chevrolet", "Malibu");
        restTemplate.postForEntity("/api/cars", car, Car.class);

        // Verificando se o carro foi salvo no banco
        List<Car> found = carRepository.findAll();
        assertThat(found).extracting(Car::getMake).containsOnly("Chevrolet");
    }

    @Test
    void shouldGetAllCars() {
        // Criando carros para teste
        createCar("BMW", "X5");
        createCar("Audi", "A4");

        // Realizando requisição GET para obter todos os carros
        ResponseEntity<List<Car>> response = restTemplate.exchange(
                "/api/cars",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Car>>() {});

        // Verificando se a resposta está correta
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).extracting(Car::getMake).containsExactly("BMW", "Audi");
    }


    // Função auxiliar para criar e salvar carros
    private void createCar(String make, String model) {
        Car car = new Car(make, model);
        carRepository.saveAndFlush(car);
    }
}

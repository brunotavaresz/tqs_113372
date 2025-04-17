package com.example;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import com.example.*;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
class DemoApplicationTests {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
			"postgres:16-alpine");

	@Autowired
	private carRepository repository;

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
	}

	@Test
	@Order(1)
	void insertCar() {
		repository.save(new Car("audi", "a4", 2021));
		assertThat(repository.count()).isEqualTo(1);
		assertThat(repository.findAll()).extracting(Car::getMaker).containsOnly("audi");
	}

	@Test
	@Order(2)
	void alterCar() {
		Car car = repository.findAll().get(0);
		car.setMaker("bmw");
		repository.save(car);
		assertThat(repository.count()).isEqualTo(1);
		assertThat(repository.findAll()).extracting(Car::getMaker).containsOnly("bmw");
	}

	@Test
	@Order(3)
	void deleteCar() {
		repository.deleteAll();
		assertThat(repository.count()).isEqualTo(0);
	}

}
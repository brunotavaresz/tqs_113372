package com.example.cars;

import org.springframework.boot.SpringApplication;

import com.example.cars.CarsApplication;

public class TestDemoApplication {

	public static void main(String[] args) {
		SpringApplication.from(CarsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
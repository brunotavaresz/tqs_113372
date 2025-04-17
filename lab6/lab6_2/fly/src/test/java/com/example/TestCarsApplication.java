package com.example;

import org.springframework.boot.SpringApplication;

public class TestCarsApplication {

	public static void main(String[] args) {
		SpringApplication.from(CarsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

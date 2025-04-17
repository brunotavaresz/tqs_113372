package com.example.cars.object;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;
    private String make;
    private String model;
    private String segment;    
    private String engineType; 

    public Car() {}

    public Car(String make, String model) {
        this.make = make;
        this.model = model;
    }

    public Long getCarId() {
        return carId;
    }
    

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return carId != null && carId.equals(car.carId); // Comparando pelo ID ou outras propriedades se necessário
    }

    @Override
    public int hashCode() {
        return 31 + (carId != null ? carId.hashCode() : 0); // Usando carId para o hash
    }

}

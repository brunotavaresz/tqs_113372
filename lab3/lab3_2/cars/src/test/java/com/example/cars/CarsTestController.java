package com.example.cars;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;


import java.util.Arrays;
import java.util.List;

import com.example.cars.controller.CarsController;
import com.example.cars.object.Car;
import com.example.cars.service.CarManagerService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;


@WebMvcTest(CarsController.class)
public class CarsTestController {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CarManagerService service;
    
    @Test
    public void testCreateCar() throws Exception {

        Car car = new Car("Ford", "Focus");
        when(service.save(Mockito.any())).thenReturn(car);
        
        mvc.perform(
                post("/api/cars").contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(car)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.make", is("Ford")));

        verify(service, times(1)).save(Mockito.any());
    }

    @Test
    public void testGetAllCars() throws Exception {
        Car car1 = new Car("Ford", "Focus");
        Car car2 = new Car("Toyota", "Corolla");
        Car car3 = new Car("Honda", "Civic");

        List <Car> cars = Arrays.asList(car1, car2, car3);

        when(service.getAllCars()).thenReturn(cars);

        mvc.perform(get("/api/cars").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].make", is("Ford")))
                .andExpect(jsonPath("$[1].make", is("Toyota")))
                .andExpect(jsonPath("$[2].make", is("Honda")));

        verify(service, times(1)).getAllCars();
    }
    
    @Test
    public void testGetCarById() throws Exception {
        Car car = new Car("Mercedes", "SLK200");
        car.setCarId(1L);

        when(service.getCarDetails(1L)).thenReturn(car);

        mvc.perform(get("/api/cars/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.make", is("Mercedes")))
                .andExpect(jsonPath("$.model", is("SLK200")));

        verify(service, times(1)).getCarDetails(1L);
    }

}

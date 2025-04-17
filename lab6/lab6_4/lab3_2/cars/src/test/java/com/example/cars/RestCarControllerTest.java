import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import tqs.cars.car.CarApplication;
import tqs.cars.car.Controller.CarController;
import tqs.cars.car.Service.CarManagerService;
import tqs.cars.car.model.Car;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;

@WebMvcTest(CarController.class)
@ContextConfiguration(classes = CarApplication.class)
public class RestCarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarManagerService carManagerService;

    @AfterEach
    public void resetDb() {
        reset(carManagerService);
    }

    @Test
    public void whenPostCar_thenCreateCar() throws Exception {
        Car car = new Car("Toyota", "Corolla");
        when(carManagerService.getAllCars()).thenReturn(List.of(car));

        RestAssuredMockMvc.given()
                .mockMvc(mockMvc)
                .when().get("/api/cars")
                .then()
                .statusCode(200)
                .body("maker", hasItem("Toyota"));
    }
}
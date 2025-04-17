
import java.beans.Transient;

import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static java.util.concurrent.TimeUnit.*;
import static io.restassured.matcher.RestAssuredMatchers.*;

public class MainTest {

    @Test
    public void testAPI() {
        when().get("https://jsonplaceholder.typicode.com/todos").then().statusCode(200);
    }

    @Test
    public void testAPI2() {
        when().get("https://jsonplaceholder.typicode.com/todos/4").then().statusCode(200).body("title",
                equalTo("et porro tempora"));
    }

    @Test
    public void testAPI3() {
        when().get("https://jsonplaceholder.typicode.com/todos").then().statusCode(200).body("id",
                hasItems(198, 199));
    }

    @Test
    public void testAPI4() {
        // Please note that response time measurement should be performed when the JVM
        // is hot! (i.e. running a response time measurement when only running a single
        // test will yield erroneous results). Also note that you can only vaguely
        // regard these measurments to correlate with the server request processing time
        // (since the response time will include the HTTP round trip and REST Assured
        // processing time among other things).
        // Adicionar uma chamada só para garantir que o JVM está quente
        when().get("https://jsonplaceholder.typicode.com/todos").then().statusCode(200);
        when().get("https://jsonplaceholder.typicode.com/todos").then().time(lessThan(2L), SECONDS);
    }

}
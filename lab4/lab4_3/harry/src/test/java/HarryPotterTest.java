import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class HarryPotterTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void teardown() {
        driver.quit();
    }

    @Test
    void test() {
        driver.get("https://cover-bookstore.onrender.com/");

        wait.until(ExpectedConditions.titleContains("Cover - Find your favorite books."));
        assertThat(driver.getTitle()).contains("Cover - Find your favorite books.");
        
        WebElement searchElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid=book-search-input]")));
        searchElement.sendKeys("Harry Potter");
        searchElement.sendKeys(Keys.RETURN);

        WebElement bookTitleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("SearchList_bookTitle__1wo4a")));
        
        assertThat(bookTitleElement.getText()).contains("Harry Potter and the Sorcerer's Stone");
    }
}

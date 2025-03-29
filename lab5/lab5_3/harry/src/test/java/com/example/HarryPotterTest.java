package com.example;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HarryPotterTest {
   WebDriver driver;
   WebDriverWait wait;

   @When("I navigate to {string}")
   public void i_navigate_to(String url) {
      WebDriverManager.chromedriver().setup();
      ChromeOptions options = new ChromeOptions();
      driver = new ChromeDriver(options);
      driver.get(url);
      wait = new WebDriverWait(driver, Duration.ofSeconds(15));  // Aguarde até 15s
   }

   @And("I search for {string}")
   public void i_search_for(String bookTitle) {
      WebElement searchElement = driver.findElement(By.cssSelector("[data-testid=book-search-input]"));
      searchElement.sendKeys(bookTitle);
   }

   @And("I click submit")
   public void i_press_enter() {
      WebElement searchElement = driver.findElement(By.cssSelector("[data-testid=book-search-input]"));
      searchElement.sendKeys(Keys.RETURN);
   }

   @Then("the user should see {string} in the search results")
   public void the_user_should_see(String expectedTitle) {
      WebElement bookTitleElement = wait.until(
         ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".SearchList_bookTitle__1wo4a"))
      );
      Assertions.assertThat(bookTitleElement.getText()).contains(expectedTitle);
      driver.quit();
   }
}

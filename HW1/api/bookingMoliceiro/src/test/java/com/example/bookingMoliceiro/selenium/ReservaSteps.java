package com.example.bookingMoliceiro.selenium;

import io.cucumber.java.en.*;
import io.cucumber.java.After;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

public class ReservaSteps {
   WebDriver driver;
   WebDriverWait wait;
   
   @Given("o usuário acessa a aplicação em {string}")
   public void oUsuarioAcessaAplicacao(String url) {
      WebDriverManager.chromedriver().setup();
      ChromeOptions options = new ChromeOptions();
      driver = new ChromeDriver(options);
      driver.get(url);
      
      // Espera implícita para todos os elementos
      driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
      
      // WebDriverWait para condições específicas
      wait = new WebDriverWait(driver, Duration.ofSeconds(15));
   }
   
   @When("o usuário navega até a página de reserva")
   public void oUsuarioNavegaParaReserva() {
      WebElement botaoReservar = driver.findElement(By.linkText("Reservar"));
      botaoReservar.click();
   }
   
   @And("seleciona um restaurante da lista")
   public void selecionaRestaurante() {
      WebElement restaurante = driver.findElement(By.cssSelector(".MuiCardMedia-root"));
      restaurante.click();
      WebElement botaoReservar = driver.findElement(By.cssSelector(".MuiButton-containedSuccess"));
      botaoReservar.click();
   }
   
   @And("preenche o nome do cliente como {string}")
   public void preencheNomeCliente(String nome) {
      WebElement campoNome = driver.findElement(By.id("customerName"));
      campoNome.sendKeys(nome);
      WebElement botaoConfirmar = driver.findElement(By.cssSelector(".css-18jze8d-MuiButtonBase-root-MuiButton-root"));
      botaoConfirmar.click();
   }
   
   @Then("a reserva é efetuada com a mensagem {string}")
   public void reservaEfetuadaComMensagem(String mensagemEsperada) {
      WebElement mensagem = wait.until(
         ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".MuiTypography-root.MuiTypography-h5.MuiTypography-gutterBottom.css-3o0yx0-MuiTypography-root")
         )
      );
      // Verifica se a mensagem exibida é exatamente a esperada
      assertEquals(mensagemEsperada, mensagem.getText());
      System.out.println("Reserva confirmada com sucesso!");
   }
   
   @And("navega para consultar reserva")
   public void navegaParaConsultarReserva() {
      WebElement linkConsultar = driver.findElement(By.linkText("Consultar Reserva"));
      linkConsultar.click();
   }
   
   @And("clica no botão de tamanho pequeno")
   public void clicaBotaoTamanhoPequeno() {
      WebElement botaoPequeno = driver.findElement(By.cssSelector(".MuiButton-sizeSmall"));
      botaoPequeno.click();
   }
   
   @And("clica no botão contido")
   public void clicaBotaoContido() {
      WebElement botaoContido = driver.findElement(By.cssSelector(".MuiButton-contained"));
      botaoContido.click();
   }
   
   @And("confirma com o botão de sucesso")
   public void confirmaBotaoSucesso() {
      WebElement botaoSucesso = driver.findElement(By.cssSelector(".MuiButton-textSuccess"));
      botaoSucesso.click();
   }
   
   @Then("verifica se o status é {string}")
   public void verificaStatus(String statusEsperado) {
      try {
         WebElement statusElement = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
               By.cssSelector(".MuiTypography-root.MuiTypography-body1.MuiTypography-gutterBottom.css-tsh1mv-MuiTypography-root")
            )
         );
         
         // Verifica se o texto é o esperado
         assertEquals(statusEsperado, statusElement.getText());
         System.out.println("Status verificado com sucesso: " + statusElement.getText());
         
         // Se o status for "Checked In", fecha o driver
         if (statusElement.getText().equals("Checked In")) {
            driver.quit();
            System.out.println("Driver encerrado após verificar status 'Checked In'");
         }
      } catch (Exception e) {
         System.out.println("Erro ao verificar status: " + e.getMessage());
      }
   }
   
   @After
   public void tearDown() {
      // Garante que o driver seja fechado mesmo se algum teste falhar
      if (driver != null) {
         driver.quit();
         System.out.println("Driver encerrado no método tearDown");
      }
   }
}

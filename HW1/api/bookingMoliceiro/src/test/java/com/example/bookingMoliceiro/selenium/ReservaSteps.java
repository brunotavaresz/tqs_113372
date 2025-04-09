package com.example.bookingMoliceiro.selenium;

import io.cucumber.java.en.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.openqa.selenium.interactions.Actions;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class ReservaSteps {
   private WebDriver driver;
   private WebDriverWait wait;
   private Map<String, Object> vars;
   private JavascriptExecutor js;
   
   @Before
   public void setUp() {
      WebDriverManager.chromedriver().setup();
      ChromeOptions options = new ChromeOptions();
      driver = new ChromeDriver(options);
      js = (JavascriptExecutor) driver;
      vars = new HashMap<String, Object>();
      
      driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
      
      wait = new WebDriverWait(driver, Duration.ofSeconds(15));
   }
   
   @Given("o usuário acessa a aplicação em {string}")
   public void oUsuarioAcessaAplicacao(String url) {
      driver.get(url);
      driver.manage().window().setSize(new Dimension(1920, 1048));
   }
   
   @When("o usuário navega até a página de reserva")
   public void oUsuarioNavegaParaReserva() {
      WebElement botaoReservar = driver.findElement(By.linkText("Reservar"));
      botaoReservar.click();
   }
   
   @And("clica na imagem do restaurante")
   public void clicaNaImagemDoRestaurante() {
      WebElement imagemRestaurante = driver.findElement(By.cssSelector(".MuiGrid-root:nth-child(1) .MuiCardMedia-root"));
      imagemRestaurante.click();
   }
   
   @And("seleciona o primeiro horário disponível")
   public void selecionaPrimeiroHorario() {
      WebElement horario = driver.findElement(By.cssSelector(".MuiPaper-root:nth-child(1) .MuiListItem-root:nth-child(1) .MuiButtonBase-root"));
      horario.click();
   }
   
   @And("preenche {string} como nome do cliente")
   public void preencheNomeCliente(String nome) {
      WebElement campoNome = driver.findElement(By.id("customerName"));
      campoNome.click();
      campoNome.sendKeys(nome);
   }
   
   @And("pressiona Enter")
   public void pressionaEnter() {
      WebElement campoNome = driver.findElement(By.id("customerName"));
      campoNome.sendKeys(Keys.ENTER);
   }
   
   @And("confirma a reserva")
   public void confirmaReserva() {
      WebElement botaoConfirmar = driver.findElement(By.cssSelector(".MuiDialogActions-root > .MuiButton-contained"));
      botaoConfirmar.click();
   }
   
   @And("navega para consultar reserva")
   public void navegaParaConsultarReserva() {
      WebElement linkConsultar = driver.findElement(By.linkText("Consultar Reserva"));
      linkConsultar.click();
   }
   
   @And("clica na reserva")
   public void clicaNaReserva() {
      // Aguardar elemento ficar visível e clicável
      WebElement elementoReserva = wait.until(ExpectedConditions.elementToBeClickable(
          By.cssSelector(".MuiButton-sizeSmall, .MuiGrid-root:nth-child(2) .MuiButtonBase-root")
      ));
      elementoReserva.click();
   }
   
   @And("clica no botão de confirmar")
   public void clicaBotaoConfirmar() {
      WebElement botaoConfirmar = driver.findElement(By.cssSelector(".MuiButton-contained"));
      botaoConfirmar.click();
   }
   
   @And("clica no botão de sucesso")
   public void clicaBotaoSucesso() {
      WebElement botaoSucesso = wait.until(ExpectedConditions.elementToBeClickable(
          By.cssSelector(".MuiButton-textSuccess")
      ));
      botaoSucesso.click();
   }
   
   @Then("o status deve ser {string}")
   public void verificaStatus(String statusEsperado) {
      try {
         // Espera pelo elemento com o status "Checked In"
         WebElement statusElement = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
               By.cssSelector(".MuiTypography-root.MuiTypography-body1.MuiTypography-gutterBottom.css-tsh1mv-MuiTypography-root")
            )
         );
         
         // Verifica se o texto é exatamente o esperado
         assertEquals(statusEsperado, statusElement.getText());
         System.out.println("Status verificado com sucesso: " + statusElement.getText());
      } catch (Exception e) {
         System.out.println("Erro ao verificar status: " + e.getMessage());
         fail("Não foi possível verificar o status: " + e.getMessage());
      }
   }
   
   @After
   public void tearDown() {
      // Garante que o driver seja fechado após o teste
      if (driver != null) {
         driver.quit();
         System.out.println("Driver encerrado no método tearDown");
      }
   }
}
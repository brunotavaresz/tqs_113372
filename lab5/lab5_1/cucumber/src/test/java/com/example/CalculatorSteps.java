package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CalculatorSteps {

    private Calculator calc;

    @Given("a calculator I just turned on")
    public void setup() {
        calc = new Calculator();
    }

    @When("I add {int} and {int}")
    public void add(int arg1, int arg2) {
        calc.push(arg1);
        calc.push(arg2);
        calc.push("+");
    }

    @When("I subtract {int} to {int}")   //troquei os args de ordem
    public void substract(int arg1, int arg2) {
        calc.push(arg2);
        calc.push(arg1);
        calc.push("-");
    }

    @When("I multiply {int} by {int}")
    public void multiplication(int arg1, int arg2) {
        calc.push(arg1);
        calc.push(arg2);
        calc.push("*");
    }

    @When("I divide {int} by {int}")
    public void division(int arg1, int arg2) {
        calc.push(arg1);
        calc.push(arg2);
        calc.push("/");
    }

    @Then("the result was an error")
    public void the_result_error(){
        assertEquals(Double.POSITIVE_INFINITY, calc.value());
    }

    @Then("the result is {double}")    
    public void the_result_is(double expected) {
        assertEquals(expected, calc.value());
    }

}
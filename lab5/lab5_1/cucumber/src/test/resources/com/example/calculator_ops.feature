@calc_sample
Feature: Basic Arithmetic
  Background: A Calculator
    Given a calculator I just turned on
  Scenario: Addition
    When I add 4 and 7
    Then the result is 11
  Scenario: Substraction
    When I subtract 2 to 7
    Then the result is 5
  Scenario Outline: Several additions
    When I add <a> and <b>
    Then the result is <c>
  Examples: Single digits
    | a | b | c  |
    | 1 | 2 | 3  |
    | 3 | 7 | 10 |
  Scenario: Multiplication
    When I multiply 8 by 3
    Then the result is 24
  Scenario: Division
    When I divide 10 by 2
    Then the result is 5
  Scenario Outline: Several divisions
    When I divide <a> by <b>
    Then the result is <c>
  Examples: Operations
    | a | b  | c    |
    | 3 | 4  | 0.75 |
    | 2 | 10 | 0.2  |
  Scenario: Invalid Operations
    When I divide 1 by 0
    Then the result was an error
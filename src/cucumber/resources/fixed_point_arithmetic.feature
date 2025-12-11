Feature: Fixed-Point Arithmetic for Low-Latency Trading
  As a low-latency trading system developer
  I want to use fixed-point arithmetic with primitives
  So that I can avoid floating-point precision errors and GC pressure

  Scenario: Create price from string representation
    Given I have a price string "123.456"
    When I create a FixedPointPrice from the string
    Then the price should have scaled value 12345600
    And the price should display as "123.45600"

  Scenario: Add two prices without floating-point errors
    Given I have a price "0.1"
    And I have another price "0.2"
    When I add the two prices
    Then the result should be exactly "0.30000"
    And there should be no floating-point precision errors

  Scenario: Multiply price by quantity
    Given I have a price "10.50"
    And I have a quantity of 5
    When I multiply the price by the quantity
    Then the total value should be "52.50000"

  Scenario: Calculate price spread
    Given I have a bid price "100.50"
    And I have an ask price "100.75"
    When I calculate the spread
    Then the spread should be "0.25000"

  Scenario: Compare prices using primitives
    Given I have a price "100.50"
    And I have another price "100.25"
    When I compare the prices
    Then the first price should be greater than the second
    And the comparison should use primitive long values

  Scenario: Create quantity from different formats
    Given I have a quantity string "1000.500"
    When I create a FixedPointQuantity from the string
    Then the quantity scaled value should be 1000500
    And the quantity should display as "1000.500"

  Scenario: Perform quantity calculations
    Given I have a quantity "100.500"
    And I have another quantity "50.250"
    When I add the quantities
    Then the total quantity should be "150.750"

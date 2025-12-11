Feature: Order Book with Primitive Data Structures
  As a trading system developer
  I want to use primitive-based data structures for the order book
  So that I can minimize GC pauses and maximize throughput

  Background:
    Given I have an empty order book

  Scenario: Add bid orders in price-time priority
    When I add a bid at price 100.00 with quantity 1000
    And I add a bid at price 105.00 with quantity 500
    And I add a bid at price 95.00 with quantity 750
    Then the order book should have 3 bid levels
    And the best bid should be 105.00
    And the bids should be ordered 105.00, 100.00, 95.00

  Scenario: Add ask orders in price-time priority
    When I add an ask at price 110.00 with quantity 1000
    And I add an ask at price 105.00 with quantity 500
    And I add an ask at price 115.00 with quantity 750
    Then the order book should have 3 ask levels
    And the best ask should be 105.00
    And the asks should be ordered 105.00, 110.00, 115.00

  Scenario: Calculate bid-ask spread
    When I add a bid at price 100.00 with quantity 1000
    And I add an ask at price 105.00 with quantity 500
    Then the spread should be 5.00

  Scenario: Calculate total volumes
    When I add a bid at price 100.00 with quantity 1000
    And I add a bid at price 99.50 with quantity 500
    And I add a bid at price 99.00 with quantity 750
    Then the total bid volume should be 2250

  Scenario: Remove top of book orders
    Given I add a bid at price 105.00 with quantity 500
    And I add a bid at price 100.00 with quantity 1000
    When I remove the top bid
    Then the best bid should be 100.00
    And the order book should have 1 bid level

  Scenario: Clear order book for reuse
    Given I add a bid at price 100.00 with quantity 1000
    And I add an ask at price 105.00 with quantity 500
    When I clear the order book
    Then the order book should have 0 bid levels
    And the order book should have 0 ask levels
    And the arrays should remain allocated for reuse

  Scenario: Handle large order book efficiently
    When I add 150 bid orders
    And I add 150 ask orders
    Then the order book should handle capacity growth
    And all orders should be stored in primitive arrays

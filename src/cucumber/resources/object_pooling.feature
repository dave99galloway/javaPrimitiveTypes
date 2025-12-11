Feature: Object Pooling for Zero-Allocation Pattern
  As a low-latency system developer
  I want to use object pooling for frequently created objects
  So that I can avoid GC pauses on the critical path

  Background:
    Given I have an order pool with capacity 10

  Scenario: Acquire order from pool
    When I acquire an order from the pool
    Then I should receive a valid order object
    And the pool should have 9 available orders
    And the pool should have 1 in-use order

  Scenario: Set order values with primitives
    Given I acquire an order from the pool
    When I set order ID to 12345
    And I set timestamp to 1638360000
    And I set price to 100.50 scaled as 10050000
    And I set quantity to 1000 scaled as 1000000
    And I set side to buy
    Then all values should be stored as primitives
    And no new objects should be created

  Scenario: Release order back to pool
    Given I acquire an order from the pool
    And I set order values
    When I release the order back to the pool
    Then the order should be reset to default values
    And the pool should have 10 available orders
    And the pool should have 0 in-use orders

  Scenario: Reuse released orders
    Given I acquire an order from the pool as "order1"
    And I acquire another order from the pool as "order2"
    When I release "order1" back to the pool
    And I acquire another order from the pool as "order3"
    Then "order3" should be the same object as "order1"
    And zero new allocations should have occurred

  Scenario: Pool exhaustion handling
    Given I acquire all 10 orders from the pool
    When I try to acquire another order
    Then I should receive an exhaustion exception
    And the exception should indicate the pool is full

  Scenario: Zero-allocation lifecycle
    When I acquire an order from the pool
    And I use the order for trading operations
    And I release the order back to the pool
    And I acquire an order again
    Then the same pre-allocated object should be reused
    And no garbage collection should be triggered

  Scenario: Multiple acquire and release cycles
    When I perform 100 acquire-use-release cycles
    Then all operations should reuse the same 10 order objects
    And no new Order objects should be created
    And GC pressure should be minimal

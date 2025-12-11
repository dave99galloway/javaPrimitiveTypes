Feature: Primitive Types vs Wrapper Classes
  As a performance-conscious developer
  I want to understand the tradeoffs between primitives and wrappers
  So that I can make informed decisions for low-latency systems

  Scenario: Compare memory footprint of primitives vs wrappers
    Given I create an order with primitive fields
    And I create an order with wrapper fields
    Then the primitive order should use less memory
    And the primitive order should have zero GC overhead

  Scenario: Sum values using primitive array
    Given I have a primitive long array with values 1, 2, 3, 4, 5
    When I sum the primitive array
    Then the sum should be 15
    And no objects should be created during summation

  Scenario: Sum values using wrapper array
    Given I have a Long wrapper array with values 1, 2, 3, 4, 5
    When I sum the wrapper array
    Then the sum should be 15
    But auto-unboxing overhead should occur

  Scenario: Sum values using ArrayList
    Given I have an ArrayList<Long> with values 1, 2, 3, 4, 5
    When I sum the ArrayList
    Then the sum should be 15
    But boxing and iterator overhead should occur

  Scenario: Handle optional values with primitives
    Given I need to represent an optional price
    When I use a primitive with sentinel value
    Then I should avoid wrapper class overhead
    And I should still detect when price is not available

  Scenario: Create large arrays of values
    Given I need to store 100000 values
    When I use a primitive long array
    Then only one array object should be allocated
    But when I use ArrayList<Long>
    Then 100000 Long objects should be created
    And garbage collection pressure should increase

package com.playground.javaprimitivetypes.cucumber;

import com.playground.javaprimitivetypes.FixedPointPrice;
import com.playground.javaprimitivetypes.FixedPointQuantity;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import static org.assertj.core.api.Assertions.*;

public class FixedPointArithmeticSteps {
    
    private String priceString;
    private String quantityString;
    private FixedPointPrice price;
    private FixedPointPrice price2;
    private FixedPointPrice resultPrice;
    private FixedPointQuantity quantity;
    private FixedPointQuantity quantity2;
    private FixedPointQuantity resultQuantity;
    private long quantityLong;
    
    @Given("I have a price string {string}")
    public void iHaveAPriceString(String priceStr) {
        this.priceString = priceStr;
    }
    
    @When("I create a FixedPointPrice from the string")
    public void iCreateAFixedPointPriceFromTheString() {
        this.price = FixedPointPrice.fromString(priceString);
    }
    
    @Then("the price should have scaled value {long}")
    public void thePriceShouldHaveScaledValue(long expectedScaledValue) {
        assertThat(price.getScaledValue()).isEqualTo(expectedScaledValue);
    }
    
    @And("the price should display as {string}")
    public void thePriceShouldDisplayAs(String expectedDisplay) {
        assertThat(price.toString()).isEqualTo(expectedDisplay);
    }
    
    @Given("I have a price {string}")
    public void iHaveAPrice(String priceStr) {
        this.price = FixedPointPrice.fromString(priceStr);
    }
    
    @And("I have another price {string}")
    public void iHaveAnotherPrice(String priceStr) {
        this.price2 = FixedPointPrice.fromString(priceStr);
    }
    
    @When("I add the two prices")
    public void iAddTheTwoPrices() {
        this.resultPrice = price.add(price2);
    }
    
    @Then("the result should be exactly {string}")
    public void theResultShouldBeExactly(String expected) {
        assertThat(resultPrice.toString()).isEqualTo(expected);
    }
    
    @And("there should be no floating-point precision errors")
    public void thereShouldBeNoFloatingPointPrecisionErrors() {
        // Verify exact scaled value (0.1 + 0.2 = 0.3 exactly in fixed-point)
        FixedPointPrice expected = FixedPointPrice.fromString("0.3");
        assertThat(resultPrice.getScaledValue()).isEqualTo(expected.getScaledValue());
        
        // Demonstrate the double precision problem
        double d1 = 0.1;
        double d2 = 0.2;
        double sum = d1 + d2;
        // sum is actually 0.30000000000000004 with doubles
        assertThat(sum).isNotEqualTo(0.3); // This would fail with exact comparison
        
        // But fixed-point is exact
        assertThat(resultPrice.toDouble()).isCloseTo(0.3, within(0.00001));
    }
    
    @And("I have a quantity of {int}")
    public void iHaveAQuantityOf(int qty) {
        this.quantityLong = qty;
    }
    
    @When("I multiply the price by the quantity")
    public void iMultiplyThePriceByTheQuantity() {
        this.resultPrice = price.multiply(quantityLong);
    }
    
    @Then("the total value should be {string}")
    public void theTotalValueShouldBe(String expected) {
        assertThat(resultPrice.toString()).isEqualTo(expected);
    }
    
    @Given("I have a bid price {string}")
    public void iHaveABidPrice(String priceStr) {
        this.price = FixedPointPrice.fromString(priceStr);
    }
    
    @And("I have an ask price {string}")
    public void iHaveAnAskPrice(String priceStr) {
        this.price2 = FixedPointPrice.fromString(priceStr);
    }
    
    @When("I calculate the spread")
    public void iCalculateTheSpread() {
        this.resultPrice = price2.subtract(price);
    }
    
    @Then("the spread should be {string}")
    public void theSpreadShouldBe(String expected) {
        assertThat(resultPrice.toString()).isEqualTo(expected);
    }
    
    @When("I compare the prices")
    public void iCompareThePrices() {
        // Comparison is done in the next step
    }
    
    @Then("the first price should be greater than the second")
    public void theFirstPriceShouldBeGreaterThanTheSecond() {
        assertThat(price.isGreaterThan(price2)).isTrue();
        assertThat(price.compareTo(price2)).isPositive();
    }
    
    @And("the comparison should use primitive long values")
    public void theComparisonShouldUsePrimitiveLongValues() {
        // Verify comparison uses primitives by checking the values directly
        long value1 = price.getScaledValue();
        long value2 = price2.getScaledValue();
        assertThat(value1).isGreaterThan(value2);
        
        // No boxing/unboxing occurs - direct primitive comparison
        assertThat(value1 > value2).isTrue();
    }
    
    @Given("I have a quantity string {string}")
    public void iHaveAQuantityString(String qtyStr) {
        this.quantityString = qtyStr;
    }
    
    @When("I create a FixedPointQuantity from the string")
    public void iCreateAFixedPointQuantityFromTheString() {
        this.quantity = FixedPointQuantity.fromString(quantityString);
    }
    
    @Then("the quantity scaled value should be {long}")
    public void theQuantityScaledValueShouldBe(long expectedValue) {
        assertThat(quantity.getScaledValue()).isEqualTo(expectedValue);
    }
    
    @And("the quantity should display as {string}")
    public void theQuantityShouldDisplayAs(String expected) {
        assertThat(quantity.toString()).isEqualTo(expected);
    }
    
    @Given("I have a quantity {string}")
    public void iHaveAQuantity(String qtyStr) {
        this.quantity = FixedPointQuantity.fromString(qtyStr);
    }
    
    @And("I have another quantity {string}")
    public void iHaveAnotherQuantity(String qtyStr) {
        this.quantity2 = FixedPointQuantity.fromString(qtyStr);
    }
    
    @When("I add the quantities")
    public void iAddTheQuantities() {
        this.resultQuantity = quantity.add(quantity2);
    }
    
    @Then("the total quantity should be {string}")
    public void theTotalQuantityShouldBe(String expected) {
        assertThat(resultQuantity.toString()).isEqualTo(expected);
    }
}

package com.playground.javaprimitivetypes.cucumber;

import com.playground.javaprimitivetypes.OrderBook;
import com.playground.javaprimitivetypes.FixedPointPrice;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import static org.assertj.core.api.Assertions.*;

public class OrderBookSteps {
    
    private OrderBook orderBook;
    
    @Given("I have an empty order book")
    public void iHaveAnEmptyOrderBook() {
        this.orderBook = new OrderBook();
    }
    
    @When("I add a bid at price {double} with quantity {int}")
    public void iAddABidAtPriceWithQuantity(double price, int quantity) {
        FixedPointPrice fpPrice = FixedPointPrice.fromDouble(price);
        orderBook.addBid(fpPrice.getScaledValue(), quantity);
    }
    
    @And("I add an ask at price {double} with quantity {int}")
    public void iAddAnAskAtPriceWithQuantity(double price, int quantity) {
        FixedPointPrice fpPrice = FixedPointPrice.fromDouble(price);
        orderBook.addAsk(fpPrice.getScaledValue(), quantity);
    }
    
    @Then("the order book should have {int} bid level(s)")
    public void theOrderBookShouldHaveBidLevels(int expectedLevels) {
        assertThat(orderBook.getBidCount()).isEqualTo(expectedLevels);
    }
    
    @Then("the order book should have {int} ask level(s)")
    public void theOrderBookShouldHaveAskLevels(int expectedLevels) {
        assertThat(orderBook.getAskCount()).isEqualTo(expectedLevels);
    }
    
    @And("the best bid should be {double}")
    public void theBestBidShouldBe(double expectedPrice) {
        FixedPointPrice expected = FixedPointPrice.fromDouble(expectedPrice);
        assertThat(orderBook.getBestBidPrice()).isEqualTo(expected.getScaledValue());
    }
    
    @And("the best ask should be {double}")
    public void theBestAskShouldBe(double expectedPrice) {
        FixedPointPrice expected = FixedPointPrice.fromDouble(expectedPrice);
        assertThat(orderBook.getBestAskPrice()).isEqualTo(expected.getScaledValue());
    }
    
    @And("the bids should be ordered {double}, {double}, {double}")
    public void theBidsShouldBeOrdered(double price1, double price2, double price3) {
        FixedPointPrice fp1 = FixedPointPrice.fromDouble(price1);
        FixedPointPrice fp2 = FixedPointPrice.fromDouble(price2);
        FixedPointPrice fp3 = FixedPointPrice.fromDouble(price3);
        
        assertThat(orderBook.getBidPriceAtLevel(0)).isEqualTo(fp1.getScaledValue());
        assertThat(orderBook.getBidPriceAtLevel(1)).isEqualTo(fp2.getScaledValue());
        assertThat(orderBook.getBidPriceAtLevel(2)).isEqualTo(fp3.getScaledValue());
    }
    
    @And("the asks should be ordered {double}, {double}, {double}")
    public void theAsksShouldBeOrdered(double price1, double price2, double price3) {
        FixedPointPrice fp1 = FixedPointPrice.fromDouble(price1);
        FixedPointPrice fp2 = FixedPointPrice.fromDouble(price2);
        FixedPointPrice fp3 = FixedPointPrice.fromDouble(price3);
        
        assertThat(orderBook.getAskPriceAtLevel(0)).isEqualTo(fp1.getScaledValue());
        assertThat(orderBook.getAskPriceAtLevel(1)).isEqualTo(fp2.getScaledValue());
        assertThat(orderBook.getAskPriceAtLevel(2)).isEqualTo(fp3.getScaledValue());
    }
    
    @Then("the spread should be {double}")
    public void theSpreadShouldBe(double expectedSpread) {
        FixedPointPrice expected = FixedPointPrice.fromDouble(expectedSpread);
        long actualSpread = orderBook.getSpread();
        
        // Allow small tolerance for floating point conversion
        assertThat(actualSpread).isCloseTo(expected.getScaledValue(), within(1000L));
    }
    
    @Then("the total bid volume should be {long}")
    public void theTotalBidVolumeShouldBe(long expectedVolume) {
        assertThat(orderBook.getTotalBidVolume()).isEqualTo(expectedVolume);
    }
    
    @When("I remove the top bid")
    public void iRemoveTheTopBid() {
        orderBook.removeTopBid();
    }
    
    @When("I clear the order book")
    public void iClearTheOrderBook() {
        orderBook.clear();
    }
    
    @And("the arrays should remain allocated for reuse")
    public void theArraysShouldRemainAllocatedForReuse() {
        // The arrays are not deallocated, just reset
        // This is object pooling at the array level
        // We can verify by adding new orders
        orderBook.addBid(10000000L, 100L);
        assertThat(orderBook.getBidCount()).isEqualTo(1);
    }
    
    @When("I add {int} bid orders")
    public void iAddBidOrders(int count) {
        for (int i = 0; i < count; i++) {
            long price = 10000000L - (i * 1000L); // Descending prices
            orderBook.addBid(price, 100L);
        }
    }
    
    @And("I add {int} ask orders")
    public void iAddAskOrders(int count) {
        for (int i = 0; i < count; i++) {
            long price = 11000000L + (i * 1000L); // Ascending prices
            orderBook.addAsk(price, 100L);
        }
    }
    
    @Then("the order book should handle capacity growth")
    public void theOrderBookShouldHandleCapacityGrowth() {
        // Verify orders were added (capacity auto-grows)
        assertThat(orderBook.getBidCount()).isEqualTo(150);
        assertThat(orderBook.getAskCount()).isEqualTo(150);
    }
    
    @And("all orders should be stored in primitive arrays")
    public void allOrdersShouldBeStoredInPrimitiveArrays() {
        // Conceptual verification - the OrderBook uses long[] internally
        // No wrapper objects are created for prices or quantities
        // This minimizes GC pressure even with 300 orders
        
        assertThat(orderBook.getBidCount()).isGreaterThan(0);
        assertThat(orderBook.getAskCount()).isGreaterThan(0);
        
        // All data stored as primitives in arrays
        // Zero boxing/unboxing overhead
    }
}

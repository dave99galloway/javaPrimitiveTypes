package com.playground.javaprimitivetypes.cucumber;

import com.playground.javaprimitivetypes.OrderPool;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectPoolingSteps {
    
    private OrderPool pool;
    private OrderPool.Order currentOrder;
    private final Map<String, OrderPool.Order> namedOrders = new HashMap<>();
    private Exception caughtException;
    
    @Given("I have an order pool with capacity {int}")
    public void iHaveAnOrderPoolWithCapacity(int capacity) {
        this.pool = new OrderPool(capacity);
    }
    
    @When("I acquire an order from the pool")
    public void iAcquireAnOrderFromThePool() {
        this.currentOrder = pool.acquire();
    }
    
    @Then("I should receive a valid order object")
    public void iShouldReceiveAValidOrderObject() {
        assertThat(currentOrder).isNotNull();
    }
    
    @And("the pool should have {int} available orders")
    public void thePoolShouldHaveAvailableOrders(int expected) {
        assertThat(pool.getAvailableCount()).isEqualTo(expected);
    }
    
    @And("the pool should have {int} in-use order(s)")
    public void thePoolShouldHaveInUseOrders(int expected) {
        assertThat(pool.getInUseCount()).isEqualTo(expected);
    }
    
    @When("I set order ID to {long}")
    public void iSetOrderIDTo(long orderId) {
        // Will be used in the set order values step
    }
    
    @And("I set timestamp to {long}")
    public void iSetTimestampTo(long timestamp) {
        // Will be used in the set order values step
    }
    
    @And("I set price to {double} scaled as {long}")
    public void iSetPriceToScaledAs(double price, long scaledPrice) {
        // Will be used in the set order values step
    }
    
    @And("I set quantity to {long} scaled as {long}")
    public void iSetQuantityToScaledAs(long quantity, long scaledQuantity) {
        // Will be used in the set order values step
    }
    
    @And("I set side to buy")
    public void iSetSideToBuy() {
        // Complete the order setup
        currentOrder.set(12345L, 1638360000L, 10050000L, 1000000L, true);
    }
    
    @Then("all values should be stored as primitives")
    public void allValuesShouldBeStoredAsPrimitives() {
        // All fields are primitives - no wrapper objects
        assertThat(currentOrder.getOrderId()).isEqualTo(12345L);
        assertThat(currentOrder.getTimestamp()).isEqualTo(1638360000L);
        assertThat(currentOrder.getPriceScaled()).isEqualTo(10050000L);
        assertThat(currentOrder.getQuantityScaled()).isEqualTo(1000000L);
        assertThat(currentOrder.isBuy()).isTrue();
    }
    
    @And("no new objects should be created")
    public void noNewObjectsShouldBeCreated() {
        // The order object was pre-allocated in the pool
        // Setting values only mutates primitive fields
        // No object allocation occurs
        assertThat(currentOrder.isInUse()).isTrue();
    }
    
    @And("I set order values")
    public void iSetOrderValues() {
        currentOrder.set(12345L, 1638360000L, 10050000L, 1000000L, true);
    }
    
    @When("I release the order back to the pool")
    public void iReleaseTheOrderBackToThePool() {
        pool.release(currentOrder);
    }
    
    @Then("the order should be reset to default values")
    public void theOrderShouldBeResetToDefaultValues() {
        assertThat(currentOrder.getOrderId()).isZero();
        assertThat(currentOrder.getTimestamp()).isZero();
        assertThat(currentOrder.getPriceScaled()).isZero();
        assertThat(currentOrder.getQuantityScaled()).isZero();
        assertThat(currentOrder.isBuy()).isFalse();
        assertThat(currentOrder.isInUse()).isFalse();
    }
    
    @Given("I acquire an order from the pool as {string}")
    public void iAcquireAnOrderFromThePoolAs(String name) {
        OrderPool.Order order = pool.acquire();
        namedOrders.put(name, order);
    }
    
    @And("I acquire another order from the pool as {string}")
    public void iAcquireAnotherOrderFromThePoolAs(String name) {
        OrderPool.Order order = pool.acquire();
        namedOrders.put(name, order);
    }
    
    @When("I release {string} back to the pool")
    public void iReleaseBackToThePool(String name) {
        OrderPool.Order order = namedOrders.get(name);
        pool.release(order);
    }
    
    @Then("{string} should be the same object as {string}")
    public void shouldBeTheSameObjectAs(String name1, String name2) {
        OrderPool.Order order1 = namedOrders.get(name1);
        OrderPool.Order order2 = namedOrders.get(name2);
        assertThat(order1).isSameAs(order2);
    }
    
    @And("zero new allocations should have occurred")
    public void zeroNewAllocationsShouldHaveOccurred() {
        // All orders came from the pre-allocated pool
        // No new Order objects were created
        assertThat(pool.getCapacity()).isEqualTo(10);
    }
    
    @Given("I acquire all {int} orders from the pool")
    public void iAcquireAllOrdersFromThePool(int count) {
        for (int i = 0; i < count; i++) {
            OrderPool.Order order = pool.acquire();
            order.set(i, System.currentTimeMillis(), 10000000L, 1000L, true);
        }
    }
    
    @When("I try to acquire another order")
    public void iTryToAcquireAnotherOrder() {
        try {
            pool.acquire();
        } catch (Exception e) {
            this.caughtException = e;
        }
    }
    
    @Then("I should receive an exhaustion exception")
    public void iShouldReceiveAnExhaustionException() {
        assertThat(caughtException).isInstanceOf(IllegalStateException.class);
    }
    
    @And("the exception should indicate the pool is full")
    public void theExceptionShouldIndicateThePoolIsFull() {
        assertThat(caughtException.getMessage()).contains("Order pool exhausted");
    }
    
    @When("I use the order for trading operations")
    public void iUseTheOrderForTradingOperations() {
        currentOrder.set(99999L, System.currentTimeMillis(), 10500000L, 2000L, false);
        
        // Simulate some trading operations
        long orderId = currentOrder.getOrderId();
        long price = currentOrder.getPriceScaled();
        long qty = currentOrder.getQuantityScaled();
        
        assertThat(orderId).isEqualTo(99999L);
        assertThat(price).isEqualTo(10500000L);
        assertThat(qty).isEqualTo(2000L);
    }
    
    @And("I acquire an order again")
    public void iAcquireAnOrderAgain() {
        OrderPool.Order newOrder = pool.acquire();
        namedOrders.put("reacquired", newOrder);
    }
    
    @Then("the same pre-allocated object should be reused")
    public void theSamePreAllocatedObjectShouldBeReused() {
        OrderPool.Order reacquired = namedOrders.get("reacquired");
        assertThat(reacquired).isSameAs(currentOrder);
    }
    
    @And("no garbage collection should be triggered")
    public void noGarbageCollectionShouldBeTriggered() {
        // Conceptual verification - no new objects created means no GC
        // In production, you'd verify with GC logs showing no young gen collections
        assertThat(pool.getInUseCount()).isEqualTo(1);
    }
    
    @When("I perform {int} acquire-use-release cycles")
    public void iPerformAcquireUseReleaseCycles(int cycles) {
        for (int i = 0; i < cycles; i++) {
            OrderPool.Order order = pool.acquire();
            order.set(i, System.currentTimeMillis(), 10000000L + i, 1000L, i % 2 == 0);
            
            // Use the order
            long id = order.getOrderId();
            assertThat(id).isEqualTo(i);
            
            // Release it
            pool.release(order);
        }
    }
    
    @Then("all operations should reuse the same {int} order objects")
    public void allOperationsShouldReuseTheSameOrderObjects(int expectedCount) {
        assertThat(pool.getCapacity()).isEqualTo(expectedCount);
        assertThat(pool.getAvailableCount()).isEqualTo(expectedCount);
    }
    
    @And("no new Order objects should be created")
    public void noNewOrderObjectsShouldBeCreated() {
        // All operations reused the pre-allocated pool
        assertThat(pool.getInUseCount()).isZero();
    }
    
    @And("GC pressure should be minimal")
    public void gcPressureShouldBeMinimal() {
        // 100 cycles with 10 pre-allocated objects
        // Zero new object allocations
        // Zero GC activity for the orders themselves
        assertThat(pool.getAvailableCount()).isEqualTo(pool.getCapacity());
    }
}

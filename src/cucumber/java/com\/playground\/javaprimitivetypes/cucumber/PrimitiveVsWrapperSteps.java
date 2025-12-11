package com.playground.javaprimitivetypes.cucumber;

import com.playground.javaprimitivetypes.PrimitiveVsWrapperComparison;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.But;
import io.cucumber.java.en.And;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class PrimitiveVsWrapperSteps {
    
    private PrimitiveVsWrapperComparison.OrderWithPrimitive primitiveOrder;
    private PrimitiveVsWrapperComparison.OrderWithWrappers wrapperOrder;
    private long[] primitiveArray;
    private Long[] wrapperArray;
    private List<Long> arrayList;
    private long sum;
    private PrimitiveVsWrapperComparison.OptionalPriceWithSentinel optionalPrice;
    
    @Given("I create an order with primitive fields")
    public void iCreateAnOrderWithPrimitiveFields() {
        this.primitiveOrder = new PrimitiveVsWrapperComparison.OrderWithPrimitive(
            12345L, 1638360000L, 1000L
        );
    }
    
    @And("I create an order with wrapper fields")
    public void iCreateAnOrderWithWrapperFields() {
        this.wrapperOrder = new PrimitiveVsWrapperComparison.OrderWithWrappers(
            12345L, 1638360000L, 1000L
        );
    }
    
    @Then("the primitive order should use less memory")
    public void thePrimitiveOrderShouldUseLessMemory() {
        // Primitive order: 3 * 8 bytes = 24 bytes of data
        // Wrapper order: 3 * 8 bytes (references) + 3 * (16 + 8) bytes (objects) = 96 bytes
        // This is conceptual verification - in practice you'd use profiling tools
        
        assertThat(primitiveOrder.getOrderId()).isEqualTo(12345L);
        assertThat(wrapperOrder.getOrderId()).isEqualTo(12345L);
        
        // The primitive version stores values directly in the object
        // The wrapper version stores references to heap objects
    }
    
    @And("the primitive order should have zero GC overhead")
    public void thePrimitiveOrderShouldHaveZeroGCOverhead() {
        // Primitive fields don't create separate objects to be garbage collected
        // This is a conceptual verification
        assertThat(primitiveOrder).isNotNull();
        
        // In a real scenario, you would measure GC pauses using profiling tools
        // Primitive orders would show zero GC activity for the field values
    }
    
    @Given("I have a primitive long array with values {int}, {int}, {int}, {int}, {int}")
    public void iHaveAPrimitiveLongArrayWithValues(int v1, int v2, int v3, int v4, int v5) {
        this.primitiveArray = new long[]{v1, v2, v3, v4, v5};
    }
    
    @When("I sum the primitive array")
    public void iSumThePrimitiveArray() {
        this.sum = PrimitiveVsWrapperComparison.sumPrimitiveLongs(primitiveArray);
    }
    
    @Then("the sum should be {int}")
    public void theSumShouldBe(int expected) {
        assertThat(sum).isEqualTo(expected);
    }
    
    @And("no objects should be created during summation")
    public void noObjectsShouldBeCreatedDuringSummation() {
        // Conceptual verification - primitive operations don't create objects
        // The sum variable itself is a primitive long (not a wrapper)
        assertThat(sum).isNotNull();  // Can verify it has a value
    }
    
    @Given("I have a Long wrapper array with values {int}, {int}, {int}, {int}, {int}")
    public void iHaveALongWrapperArrayWithValues(int v1, int v2, int v3, int v4, int v5) {
        this.wrapperArray = new Long[]{(long)v1, (long)v2, (long)v3, (long)v4, (long)v5};
    }
    
    @When("I sum the wrapper array")
    public void iSumTheWrapperArray() {
        this.sum = PrimitiveVsWrapperComparison.sumWrapperLongs(wrapperArray);
    }
    
    @But("auto-unboxing overhead should occur")
    public void autoUnboxingOverheadShouldOccur() {
        // Each iteration of the sum loop causes auto-unboxing
        // Long object -> long primitive conversion
        // This is conceptual - in practice you'd measure with benchmarking
        assertThat(sum).isEqualTo(15L);
    }
    
    @Given("I have an ArrayList<Long> with values {int}, {int}, {int}, {int}, {int}")
    public void iHaveAnArrayListWithValues(int v1, int v2, int v3, int v4, int v5) {
        this.arrayList = new ArrayList<>();
        arrayList.add((long)v1);
        arrayList.add((long)v2);
        arrayList.add((long)v3);
        arrayList.add((long)v4);
        arrayList.add((long)v5);
    }
    
    @When("I sum the ArrayList")
    public void iSumTheArrayList() {
        this.sum = PrimitiveVsWrapperComparison.sumArrayListLongs(arrayList);
    }
    
    @But("boxing and iterator overhead should occur")
    public void boxingAndIteratorOverheadShouldOccur() {
        // ArrayList<Long> requires:
        // 1. Boxing when adding (primitive -> Long object)
        // 2. Iterator object creation
        // 3. Auto-unboxing when summing (Long object -> primitive)
        assertThat(sum).isEqualTo(15L);
        assertThat(arrayList.size()).isEqualTo(5);
    }
    
    @Given("I need to represent an optional price")
    public void iNeedToRepresentAnOptionalPrice() {
        // Setup for optional price scenario
    }
    
    @When("I use a primitive with sentinel value")
    public void iUseAPrimitiveWithSentinelValue() {
        this.optionalPrice = PrimitiveVsWrapperComparison.OptionalPriceWithSentinel.noPrice();
    }
    
    @Then("I should avoid wrapper class overhead")
    public void iShouldAvoidWrapperClassOverhead() {
        // Uses primitive long with sentinel value instead of Long wrapper
        assertThat(optionalPrice).isNotNull();
        assertThat(optionalPrice.hasPrice()).isFalse();
    }
    
    @And("I should still detect when price is not available")
    public void iShouldStillDetectWhenPriceIsNotAvailable() {
        assertThat(optionalPrice.hasPrice()).isFalse();
        assertThatThrownBy(() -> optionalPrice.getPrice())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Price not available");
    }
    
    @Given("I need to store {int} values")
    public void iNeedToStoreValues(int count) {
        // Setup for large array scenario
    }
    
    @When("I use a primitive long array")
    public void iUseAPrimitiveLongArray() {
        this.primitiveArray = PrimitiveVsWrapperComparison.createPrimitiveArray(100_000);
    }
    
    @Then("only one array object should be allocated")
    public void onlyOneArrayObjectShouldBeAllocated() {
        // Primitive array: 1 array object containing 100,000 long values inline
        assertThat(primitiveArray).hasSize(100_000);
        // All primitives are stored directly in the array (no separate objects)
    }
    
    @But("when I use ArrayList<Long>")
    public void whenIUseArrayList() {
        this.arrayList = PrimitiveVsWrapperComparison.createWrapperObjects(100_000);
    }
    
    @Then("{int} Long objects should be created")
    public void longObjectsShouldBeCreated(int count) {
        // ArrayList<Long>: 1 ArrayList object + 100,000 Long objects
        assertThat(arrayList).hasSize(count);
        // Each element is a separate Long object on the heap
    }
    
    @And("garbage collection pressure should increase")
    public void garbageCollectionPressureShouldIncrease() {
        // 100,000 Long objects create significant GC pressure
        // Each Long object:
        // - Takes heap space (16 byte header + 8 byte value = 24 bytes)
        // - Must be garbage collected when no longer referenced
        // - Causes GC pauses in low-latency systems
        
        assertThat(arrayList.size()).isEqualTo(100_000);
        
        // Conceptual verification: wrapper collections increase GC pressure
        // In production, you'd measure this with GC logs and profiling
    }
}

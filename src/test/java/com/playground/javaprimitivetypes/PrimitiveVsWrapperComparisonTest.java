package com.playground.javaprimitivetypes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PrimitiveVsWrapper Comparison Tests")
class PrimitiveVsWrapperComparisonTest {
    
    @Test
    @DisplayName("Should sum primitive longs efficiently")
    void shouldSumPrimitiveLongs() {
        long[] primitives = {1L, 2L, 3L, 4L, 5L};
        
        long sum = PrimitiveVsWrapperComparison.sumPrimitiveLongs(primitives);
        
        assertThat(sum).isEqualTo(15L);
    }
    
    @Test
    @DisplayName("Should sum wrapper Longs with boxing overhead")
    void shouldSumWrapperLongs() {
        Long[] wrappers = {1L, 2L, 3L, 4L, 5L};
        
        long sum = PrimitiveVsWrapperComparison.sumWrapperLongs(wrappers);
        
        assertThat(sum).isEqualTo(15L);
    }
    
    @Test
    @DisplayName("Should sum ArrayList<Long> with collection overhead")
    void shouldSumArrayListLongs() {
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        list.add(4L);
        list.add(5L);
        
        long sum = PrimitiveVsWrapperComparison.sumArrayListLongs(list);
        
        assertThat(sum).isEqualTo(15L);
    }
    
    @Test
    @DisplayName("Should create wrapper objects in heap")
    void shouldCreateWrapperObjects() {
        List<Long> wrappers = PrimitiveVsWrapperComparison.createWrapperObjects(100);
        
        assertThat(wrappers).hasSize(100);
        assertThat(wrappers.get(0)).isEqualTo(0L);
        assertThat(wrappers.get(99)).isEqualTo(99L);
    }
    
    @Test
    @DisplayName("Should create primitive array without GC overhead")
    void shouldCreatePrimitiveArray() {
        long[] primitives = PrimitiveVsWrapperComparison.createPrimitiveArray(100);
        
        assertThat(primitives).hasSize(100);
        assertThat(primitives[0]).isEqualTo(0L);
        assertThat(primitives[99]).isEqualTo(99L);
    }
    
    @Test
    @DisplayName("OrderWithPrimitive should store values correctly")
    void shouldStoreOrderWithPrimitive() {
        PrimitiveVsWrapperComparison.OrderWithPrimitive order = 
            new PrimitiveVsWrapperComparison.OrderWithPrimitive(12345L, 1638360000L, 1000L);
        
        assertThat(order.getOrderId()).isEqualTo(12345L);
        assertThat(order.getTimestamp()).isEqualTo(1638360000L);
        assertThat(order.getQuantity()).isEqualTo(1000L);
    }
    
    @Test
    @DisplayName("OrderWithWrappers should store values correctly but with overhead")
    void shouldStoreOrderWithWrappers() {
        PrimitiveVsWrapperComparison.OrderWithWrappers order = 
            new PrimitiveVsWrapperComparison.OrderWithWrappers(12345L, 1638360000L, 1000L);
        
        assertThat(order.getOrderId()).isEqualTo(12345L);
        assertThat(order.getTimestamp()).isEqualTo(1638360000L);
        assertThat(order.getQuantity()).isEqualTo(1000L);
    }
    
    @Test
    @DisplayName("OptionalPrice should handle null correctly")
    void shouldHandleOptionalPrice() {
        PrimitiveVsWrapperComparison.OptionalPrice withPrice = 
            new PrimitiveVsWrapperComparison.OptionalPrice(10050L);
        PrimitiveVsWrapperComparison.OptionalPrice noPrice = 
            new PrimitiveVsWrapperComparison.OptionalPrice(null);
        
        assertThat(withPrice.hasPrice()).isTrue();
        assertThat(withPrice.getPrice()).isEqualTo(10050L);
        
        assertThat(noPrice.hasPrice()).isFalse();
        assertThatThrownBy(() -> noPrice.getPrice())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Price not available");
    }
    
    @Test
    @DisplayName("OptionalPriceWithSentinel should use sentinel value")
    void shouldHandleOptionalPriceWithSentinel() {
        PrimitiveVsWrapperComparison.OptionalPriceWithSentinel withPrice = 
            new PrimitiveVsWrapperComparison.OptionalPriceWithSentinel(10050L);
        PrimitiveVsWrapperComparison.OptionalPriceWithSentinel noPrice = 
            PrimitiveVsWrapperComparison.OptionalPriceWithSentinel.noPrice();
        
        assertThat(withPrice.hasPrice()).isTrue();
        assertThat(withPrice.getPrice()).isEqualTo(10050L);
        
        assertThat(noPrice.hasPrice()).isFalse();
        assertThatThrownBy(() -> noPrice.getPrice())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Price not available");
    }
    
    @Test
    @DisplayName("MemoryComparison should store primitive values efficiently")
    void shouldStoreMemoryComparison() {
        PrimitiveVsWrapperComparison.MemoryComparison memory = 
            new PrimitiveVsWrapperComparison.MemoryComparison(100L, 200L, 300L);
        
        assertThat(memory.getValue1()).isEqualTo(100L);
        assertThat(memory.getValue2()).isEqualTo(200L);
        assertThat(memory.getValue3()).isEqualTo(300L);
    }
    
    @Test
    @DisplayName("Performance: primitive array should be faster than wrapper array")
    void performanceComparisonPrimitiveVsWrapper() {
        int size = 100_000;
        
        // Create primitive array
        long[] primitives = new long[size];
        for (int i = 0; i < size; i++) {
            primitives[i] = i;
        }
        
        // Create wrapper array
        Long[] wrappers = new Long[size];
        for (int i = 0; i < size; i++) {
            wrappers[i] = (long) i; // Boxing occurs here
        }
        
        // Both should produce same result
        long primitiveSum = PrimitiveVsWrapperComparison.sumPrimitiveLongs(primitives);
        long wrapperSum = PrimitiveVsWrapperComparison.sumWrapperLongs(wrappers);
        
        assertThat(primitiveSum).isEqualTo(wrapperSum);
        
        // Note: In a real performance test, primitives would be significantly faster
        // This test just verifies correctness
    }
}

package com.playground.javaprimitivetypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates the performance tradeoffs between using primitive types vs wrapper classes.
 * 
 * Key findings for low-latency systems:
 * - Primitives: Zero GC pressure, stack-allocated, direct memory access
 * - Wrappers: Heap-allocated objects, boxing/unboxing overhead, GC pauses
 */
public class PrimitiveVsWrapperComparison {
    
    /**
     * Calculate sum using primitive long array (FAST - no GC)
     */
    public static long sumPrimitiveLongs(long[] values) {
        long sum = 0L;
        for (long value : values) {
            sum += value; // Direct primitive addition, no object creation
        }
        return sum;
    }
    
    /**
     * Calculate sum using wrapper Long array (SLOW - creates objects, causes GC)
     */
    public static long sumWrapperLongs(Long[] values) {
        long sum = 0L;
        for (Long value : values) {
            sum += value; // Auto-unboxing overhead on every iteration
        }
        return sum;
    }
    
    /**
     * Calculate sum using ArrayList<Long> (SLOWEST - generics require wrappers)
     */
    public static long sumArrayListLongs(List<Long> values) {
        long sum = 0L;
        for (Long value : values) {
            sum += value; // Auto-unboxing + iterator overhead
        }
        return sum;
    }
    
    /**
     * Demonstrate the GC impact: wrapper classes create objects on the heap
     */
    public static List<Long> createWrapperObjects(int count) {
        List<Long> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add((long) i); // Each add creates a new Long object (boxing)
        }
        return list; // All these objects must be garbage collected later
    }
    
    /**
     * Demonstrate primitive array: no GC impact
     */
    public static long[] createPrimitiveArray(int count) {
        long[] array = new long[count];
        for (int i = 0; i < count; i++) {
            array[i] = i; // Direct assignment, no object creation
        }
        return array; // Single array object, primitives stored inline
    }
    
    /**
     * Example: Using primitives for order ID (correct choice)
     */
    public static class OrderWithPrimitive {
        private final long orderId;      // 8 bytes on stack/inline in object
        private final long timestamp;    // 8 bytes
        private final long quantity;     // 8 bytes
        
        public OrderWithPrimitive(long orderId, long timestamp, long quantity) {
            this.orderId = orderId;
            this.timestamp = timestamp;
            this.quantity = quantity;
        }
        
        public long getOrderId() { return orderId; }
        public long getTimestamp() { return timestamp; }
        public long getQuantity() { return quantity; }
    }
    
    /**
     * Example: Using wrappers (wrong choice for low-latency)
     */
    public static class OrderWithWrappers {
        private final Long orderId;      // Reference to heap object
        private final Long timestamp;    // Reference to heap object
        private final Long quantity;     // Reference to heap object
        
        public OrderWithWrappers(Long orderId, Long timestamp, Long quantity) {
            this.orderId = orderId;      // Each field stores a reference
            this.timestamp = timestamp;
            this.quantity = quantity;
        }
        
        public Long getOrderId() { return orderId; }
        public Long getTimestamp() { return timestamp; }
        public Long getQuantity() { return quantity; }
    }
    
    /**
     * Demonstrate the difference in null handling
     * This is the ONLY valid reason to use wrappers in domain objects
     */
    public static class OptionalPrice {
        private final Long price; // Can be null to indicate "no price"
        
        public OptionalPrice(Long price) {
            this.price = price;
        }
        
        public boolean hasPrice() {
            return price != null;
        }
        
        public long getPrice() {
            if (price == null) {
                throw new IllegalStateException("Price not available");
            }
            return price; // Auto-unboxing
        }
    }
    
    /**
     * Better alternative: use sentinel value with primitives
     */
    public static class OptionalPriceWithSentinel {
        private static final long NO_PRICE = Long.MIN_VALUE;
        private final long price;
        
        public OptionalPriceWithSentinel(long price) {
            this.price = price;
        }
        
        public static OptionalPriceWithSentinel noPrice() {
            return new OptionalPriceWithSentinel(NO_PRICE);
        }
        
        public boolean hasPrice() {
            return price != NO_PRICE;
        }
        
        public long getPrice() {
            if (price == NO_PRICE) {
                throw new IllegalStateException("Price not available");
            }
            return price;
        }
    }
    
    /**
     * Compare memory footprint
     */
    public static class MemoryComparison {
        // Primitive version: 3 * 8 bytes = 24 bytes of data
        private final long value1;
        private final long value2;
        private final long value3;
        
        // Wrapper version would be: 3 * 8 bytes (references) + 3 * (16 bytes object header + 8 bytes value)
        // = 24 + 72 = 96 bytes total (4x more memory!)
        // Plus each Long object needs to be separately allocated and GC'd
        
        public MemoryComparison(long value1, long value2, long value3) {
            this.value1 = value1;
            this.value2 = value2;
            this.value3 = value3;
        }
        
        public long getValue1() { return value1; }
        public long getValue2() { return value2; }
        public long getValue3() { return value3; }
    }
}

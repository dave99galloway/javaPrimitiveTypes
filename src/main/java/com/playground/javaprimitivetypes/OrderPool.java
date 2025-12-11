package com.playground.javaprimitivetypes;

/**
 * Object pool implementation for Order objects to minimize GC pressure.
 * 
 * In low-latency systems, object allocation is avoided on the critical path.
 * Instead, objects are pre-allocated and reused via pooling.
 * 
 * Key principles:
 * - Pre-allocate a pool of objects at startup
 * - Acquire objects from pool instead of using 'new'
 * - Reset and return objects to pool after use
 * - Avoid creating new objects during hot path execution
 */
public class OrderPool {
    
    /**
     * Mutable Order object designed for pooling and reuse.
     * All fields are primitives to avoid nested object allocation.
     */
    public static class Order {
        // All fields are primitives - zero GC pressure
        private long orderId;
        private long timestamp;
        private long priceScaled;     // Fixed-point price
        private long quantityScaled;  // Fixed-point quantity
        private boolean isBuy;        // true = buy, false = sell
        private int status;           // 0 = new, 1 = filled, 2 = cancelled
        
        // Track if this order is currently in use
        private boolean inUse;
        
        public Order() {
            this.inUse = false;
        }
        
        /**
         * Initialize/reset the order with new values (in-place mutation)
         * This avoids creating a new object
         */
        public void set(long orderId, long timestamp, long priceScaled, 
                       long quantityScaled, boolean isBuy) {
            this.orderId = orderId;
            this.timestamp = timestamp;
            this.priceScaled = priceScaled;
            this.quantityScaled = quantityScaled;
            this.isBuy = isBuy;
            this.status = 0; // new order
            this.inUse = true;
        }
        
        /**
         * Reset the order to default state before returning to pool
         */
        public void reset() {
            this.orderId = 0L;
            this.timestamp = 0L;
            this.priceScaled = 0L;
            this.quantityScaled = 0L;
            this.isBuy = false;
            this.status = 0;
            this.inUse = false;
        }
        
        // Getters - all return primitives
        public long getOrderId() { return orderId; }
        public long getTimestamp() { return timestamp; }
        public long getPriceScaled() { return priceScaled; }
        public long getQuantityScaled() { return quantityScaled; }
        public boolean isBuy() { return isBuy; }
        public int getStatus() { return status; }
        public boolean isInUse() { return inUse; }
        
        // Setters for mutable operations
        public void setStatus(int status) { this.status = status; }
        public void setQuantity(long quantityScaled) { this.quantityScaled = quantityScaled; }
    }
    
    // Pool storage - array of pre-allocated orders
    private final Order[] pool;
    private final int capacity;
    private int nextAvailable;
    
    /**
     * Create a pool with specified capacity
     */
    public OrderPool(int capacity) {
        this.capacity = capacity;
        this.pool = new Order[capacity];
        this.nextAvailable = 0;
        
        // Pre-allocate all objects at construction time
        // This is the ONLY time we create Order objects
        for (int i = 0; i < capacity; i++) {
            pool[i] = new Order();
        }
    }
    
    /**
     * Acquire an order from the pool (zero allocation if pool not exhausted)
     */
    public Order acquire() {
        // Fast path: get from pool
        if (nextAvailable < capacity) {
            Order order = pool[nextAvailable];
            if (!order.isInUse()) {
                order.inUse = true; // Mark as in use
                nextAvailable++;
                return order;
            }
        }
        
        // Slow path: pool exhausted, scan for available order
        for (int i = 0; i < capacity; i++) {
            if (!pool[i].isInUse()) {
                pool[i].inUse = true; // Mark as in use
                return pool[i];
            }
        }
        
        // Pool completely exhausted - this is a warning condition
        // In production, you'd log a warning or throw an exception
        throw new IllegalStateException("Order pool exhausted - all " + capacity + " orders in use");
    }
    
    /**
     * Return an order to the pool for reuse
     * CRITICAL: caller must not use the order after releasing it
     */
    public void release(Order order) {
        if (order == null) {
            return;
        }
        
        // Verify this order belongs to our pool
        boolean found = false;
        for (int i = 0; i < capacity; i++) {
            if (pool[i] == order) {
                found = true;
                break;
            }
        }
        
        if (!found) {
            throw new IllegalArgumentException("Order does not belong to this pool");
        }
        
        // Reset the order and mark as available
        order.reset();
        
        // Reset the next available pointer for faster acquisition
        if (nextAvailable > 0) {
            nextAvailable = 0;
        }
    }
    
    /**
     * Get the number of orders currently in use
     */
    public int getInUseCount() {
        int count = 0;
        for (int i = 0; i < capacity; i++) {
            if (pool[i].isInUse()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Get the number of available orders
     */
    public int getAvailableCount() {
        return capacity - getInUseCount();
    }
    
    /**
     * Get the total capacity of the pool
     */
    public int getCapacity() {
        return capacity;
    }
    
    /**
     * Check if the pool is exhausted
     */
    public boolean isExhausted() {
        return getAvailableCount() == 0;
    }
    
    /**
     * Reset all orders in the pool (use with caution)
     * This should only be called when you're sure no orders are in use
     */
    public void resetAll() {
        for (int i = 0; i < capacity; i++) {
            pool[i].reset();
        }
        nextAvailable = 0;
    }
}

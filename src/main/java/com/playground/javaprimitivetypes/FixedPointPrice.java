package com.playground.javaprimitivetypes;

/**
 * Fixed-point price representation using a long primitive for low-latency trading systems.
 * Stores prices as scaled integers to avoid floating-point precision issues and GC pressure.
 * 
 * For example, $123.45600 with 5 decimal places is stored as the long value 12345600.
 * This approach provides:
 * - Exact decimal precision (no 0.1 representation errors)
 * - Zero GC pressure (primitive type, no object allocation)
 * - Fast arithmetic operations (CPU integer operations)
 * - Deterministic performance (no BigDecimal object creation)
 */
public final class FixedPointPrice {
    private static final int SCALE = 5; // 5 decimal places of precision
    private static final long SCALE_FACTOR = 100_000L; // 10^5
    
    private final long value; // The scaled value stored as a long
    
    /**
     * Private constructor - use factory methods for creation
     */
    private FixedPointPrice(long scaledValue) {
        this.value = scaledValue;
    }
    
    /**
     * Create a FixedPointPrice from a double value (use sparingly, only for initialization)
     */
    public static FixedPointPrice fromDouble(double price) {
        return new FixedPointPrice(Math.round(price * SCALE_FACTOR));
    }
    
    /**
     * Create a FixedPointPrice from a string representation like "123.456"
     */
    public static FixedPointPrice fromString(String price) {
        String[] parts = price.split("\\.");
        long wholePart = Long.parseLong(parts[0]);
        long fractionalPart = 0;
        
        if (parts.length > 1) {
            String fraction = parts[1];
            // Pad or truncate to SCALE decimal places
            if (fraction.length() < SCALE) {
                fraction = String.format("%-" + SCALE + "s", fraction).replace(' ', '0');
            } else if (fraction.length() > SCALE) {
                fraction = fraction.substring(0, SCALE);
            }
            fractionalPart = Long.parseLong(fraction);
        }
        
        long scaledValue = wholePart * SCALE_FACTOR + (wholePart < 0 ? -fractionalPart : fractionalPart);
        return new FixedPointPrice(scaledValue);
    }
    
    /**
     * Create a FixedPointPrice directly from a scaled long value (most efficient)
     */
    public static FixedPointPrice fromScaledLong(long scaledValue) {
        return new FixedPointPrice(scaledValue);
    }
    
    /**
     * Get the raw scaled value (for storage or transmission)
     */
    public long getScaledValue() {
        return value;
    }
    
    /**
     * Add two prices (fast long addition, no object creation)
     */
    public FixedPointPrice add(FixedPointPrice other) {
        return new FixedPointPrice(this.value + other.value);
    }
    
    /**
     * Subtract two prices
     */
    public FixedPointPrice subtract(FixedPointPrice other) {
        return new FixedPointPrice(this.value - other.value);
    }
    
    /**
     * Multiply price by a quantity (long value)
     */
    public FixedPointPrice multiply(long quantity) {
        return new FixedPointPrice(this.value * quantity);
    }
    
    /**
     * Multiply two fixed-point values
     */
    public FixedPointPrice multiply(FixedPointPrice other) {
        // Multiply and then divide by scale factor to maintain precision
        return new FixedPointPrice((this.value * other.value) / SCALE_FACTOR);
    }
    
    /**
     * Divide by a quantity
     */
    public FixedPointPrice divide(long divisor) {
        return new FixedPointPrice(this.value / divisor);
    }
    
    /**
     * Compare two prices
     */
    public int compareTo(FixedPointPrice other) {
        return Long.compare(this.value, other.value);
    }
    
    /**
     * Check if this price is greater than another
     */
    public boolean isGreaterThan(FixedPointPrice other) {
        return this.value > other.value;
    }
    
    /**
     * Check if this price is less than another
     */
    public boolean isLessThan(FixedPointPrice other) {
        return this.value < other.value;
    }
    
    /**
     * Convert to double (only for display/logging purposes, NOT for calculations)
     */
    public double toDouble() {
        return (double) value / SCALE_FACTOR;
    }
    
    /**
     * Convert to string for display
     */
    @Override
    public String toString() {
        long whole = value / SCALE_FACTOR;
        long fraction = Math.abs(value % SCALE_FACTOR);
        return String.format("%d.%05d", whole, fraction);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FixedPointPrice)) return false;
        FixedPointPrice other = (FixedPointPrice) obj;
        return this.value == other.value;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }
    
    /**
     * Get the scale factor used
     */
    public static int getScale() {
        return SCALE;
    }
    
    /**
     * Get the scale factor multiplier
     */
    public static long getScaleFactor() {
        return SCALE_FACTOR;
    }
}

package com.playground.javaprimitivetypes;

/**
 * Fixed-point quantity representation using a long primitive for low-latency trading systems.
 * Stores quantities as scaled integers to maintain precision while avoiding GC pressure.
 * 
 * Typical use: 1000.500 shares with 3 decimal places stored as 1000500L
 */
public final class FixedPointQuantity {
    private static final int SCALE = 3; // 3 decimal places for quantities
    private static final long SCALE_FACTOR = 1_000L; // 10^3
    
    private final long value; // The scaled value stored as a long
    
    private FixedPointQuantity(long scaledValue) {
        this.value = scaledValue;
    }
    
    public static FixedPointQuantity fromLong(long quantity) {
        return new FixedPointQuantity(quantity * SCALE_FACTOR);
    }
    
    public static FixedPointQuantity fromDouble(double quantity) {
        return new FixedPointQuantity(Math.round(quantity * SCALE_FACTOR));
    }
    
    public static FixedPointQuantity fromString(String quantity) {
        String[] parts = quantity.split("\\.");
        long wholePart = Long.parseLong(parts[0]);
        long fractionalPart = 0;
        
        if (parts.length > 1) {
            String fraction = parts[1];
            if (fraction.length() < SCALE) {
                fraction = String.format("%-" + SCALE + "s", fraction).replace(' ', '0');
            } else if (fraction.length() > SCALE) {
                fraction = fraction.substring(0, SCALE);
            }
            fractionalPart = Long.parseLong(fraction);
        }
        
        long scaledValue = wholePart * SCALE_FACTOR + (wholePart < 0 ? -fractionalPart : fractionalPart);
        return new FixedPointQuantity(scaledValue);
    }
    
    public static FixedPointQuantity fromScaledLong(long scaledValue) {
        return new FixedPointQuantity(scaledValue);
    }
    
    public long getScaledValue() {
        return value;
    }
    
    public FixedPointQuantity add(FixedPointQuantity other) {
        return new FixedPointQuantity(this.value + other.value);
    }
    
    public FixedPointQuantity subtract(FixedPointQuantity other) {
        return new FixedPointQuantity(this.value - other.value);
    }
    
    public FixedPointQuantity multiply(long factor) {
        return new FixedPointQuantity(this.value * factor);
    }
    
    public int compareTo(FixedPointQuantity other) {
        return Long.compare(this.value, other.value);
    }
    
    public boolean isGreaterThan(FixedPointQuantity other) {
        return this.value > other.value;
    }
    
    public boolean isLessThan(FixedPointQuantity other) {
        return this.value < other.value;
    }
    
    public boolean isPositive() {
        return this.value > 0;
    }
    
    public boolean isZero() {
        return this.value == 0;
    }
    
    public double toDouble() {
        return (double) value / SCALE_FACTOR;
    }
    
    public long toLong() {
        return value / SCALE_FACTOR;
    }
    
    @Override
    public String toString() {
        long whole = value / SCALE_FACTOR;
        long fraction = Math.abs(value % SCALE_FACTOR);
        return String.format("%d.%03d", whole, fraction);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FixedPointQuantity)) return false;
        FixedPointQuantity other = (FixedPointQuantity) obj;
        return this.value == other.value;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }
}

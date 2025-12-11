package com.playground.javaprimitivetypes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

@DisplayName("FixedPointPrice Tests")
class FixedPointPriceTest {
    
    @Test
    @DisplayName("Should create price from double value")
    void shouldCreateFromDouble() {
        FixedPointPrice price = FixedPointPrice.fromDouble(123.456);
        
        assertThat(price.toDouble()).isCloseTo(123.456, within(0.00001));
        assertThat(price.toString()).isEqualTo("123.45600");
    }
    
    @Test
    @DisplayName("Should create price from string")
    void shouldCreateFromString() {
        FixedPointPrice price = FixedPointPrice.fromString("99.95");
        
        assertThat(price.toDouble()).isCloseTo(99.95, within(0.00001));
        assertThat(price.getScaledValue()).isEqualTo(9995000L);
    }
    
    @Test
    @DisplayName("Should create price from scaled long")
    void shouldCreateFromScaledLong() {
        FixedPointPrice price = FixedPointPrice.fromScaledLong(12345600L);
        
        assertThat(price.toString()).isEqualTo("123.45600");
        assertThat(price.getScaledValue()).isEqualTo(12345600L);
    }
    
    @Test
    @DisplayName("Should add two prices correctly")
    void shouldAddPrices() {
        FixedPointPrice price1 = FixedPointPrice.fromString("100.50");
        FixedPointPrice price2 = FixedPointPrice.fromString("50.25");
        
        FixedPointPrice result = price1.add(price2);
        
        assertThat(result.toDouble()).isCloseTo(150.75, within(0.00001));
    }
    
    @Test
    @DisplayName("Should subtract two prices correctly")
    void shouldSubtractPrices() {
        FixedPointPrice price1 = FixedPointPrice.fromString("100.50");
        FixedPointPrice price2 = FixedPointPrice.fromString("50.25");
        
        FixedPointPrice result = price1.subtract(price2);
        
        assertThat(result.toDouble()).isCloseTo(50.25, within(0.00001));
    }
    
    @Test
    @DisplayName("Should multiply price by quantity")
    void shouldMultiplyByQuantity() {
        FixedPointPrice price = FixedPointPrice.fromString("10.50");
        
        FixedPointPrice result = price.multiply(5L);
        
        assertThat(result.toDouble()).isCloseTo(52.50, within(0.00001));
    }
    
    @Test
    @DisplayName("Should multiply two prices")
    void shouldMultiplyPrices() {
        FixedPointPrice price1 = FixedPointPrice.fromString("10.50");
        FixedPointPrice price2 = FixedPointPrice.fromString("2.00");
        
        FixedPointPrice result = price1.multiply(price2);
        
        assertThat(result.toDouble()).isCloseTo(21.00, within(0.00001));
    }
    
    @Test
    @DisplayName("Should divide price")
    void shouldDividePrices() {
        FixedPointPrice price = FixedPointPrice.fromString("100.00");
        
        FixedPointPrice result = price.divide(4L);
        
        assertThat(result.toDouble()).isCloseTo(25.00, within(0.00001));
    }
    
    @Test
    @DisplayName("Should compare prices correctly")
    void shouldComparePrices() {
        FixedPointPrice price1 = FixedPointPrice.fromString("100.50");
        FixedPointPrice price2 = FixedPointPrice.fromString("50.25");
        FixedPointPrice price3 = FixedPointPrice.fromString("100.50");
        
        assertThat(price1.compareTo(price2)).isPositive();
        assertThat(price2.compareTo(price1)).isNegative();
        assertThat(price1.compareTo(price3)).isZero();
        
        assertThat(price1.isGreaterThan(price2)).isTrue();
        assertThat(price2.isLessThan(price1)).isTrue();
    }
    
    @Test
    @DisplayName("Should handle negative prices")
    void shouldHandleNegativePrices() {
        FixedPointPrice price = FixedPointPrice.fromString("-10.50");
        
        assertThat(price.toDouble()).isCloseTo(-10.50, within(0.00001));
        assertThat(price.toString()).isEqualTo("-10.50000");
    }
    
    @Test
    @DisplayName("Should maintain precision without floating point errors")
    void shouldMaintainPrecision() {
        // 0.1 + 0.2 = 0.3 problem with doubles
        FixedPointPrice price1 = FixedPointPrice.fromString("0.1");
        FixedPointPrice price2 = FixedPointPrice.fromString("0.2");
        
        FixedPointPrice result = price1.add(price2);
        
        // With fixed-point, this should be exact
        assertThat(result.getScaledValue()).isEqualTo(30000L); // 0.30000 * 100000
        assertThat(result.toString()).isEqualTo("0.30000");
    }
    
    @Test
    @DisplayName("Should equals and hashCode work correctly")
    void shouldImplementEqualsAndHashCode() {
        FixedPointPrice price1 = FixedPointPrice.fromString("100.50");
        FixedPointPrice price2 = FixedPointPrice.fromString("100.50");
        FixedPointPrice price3 = FixedPointPrice.fromString("100.51");
        
        assertThat(price1).isEqualTo(price2);
        assertThat(price1).isNotEqualTo(price3);
        assertThat(price1.hashCode()).isEqualTo(price2.hashCode());
    }
    
    @Test
    @DisplayName("Should provide scale information")
    void shouldProvideScaleInfo() {
        assertThat(FixedPointPrice.getScale()).isEqualTo(5);
        assertThat(FixedPointPrice.getScaleFactor()).isEqualTo(100_000L);
    }
}

package com.playground.javaprimitivetypes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

@DisplayName("FixedPointQuantity Tests")
class FixedPointQuantityTest {
    
    @Test
    @DisplayName("Should create quantity from long")
    void shouldCreateFromLong() {
        FixedPointQuantity qty = FixedPointQuantity.fromLong(1000L);
        
        assertThat(qty.toLong()).isEqualTo(1000L);
        assertThat(qty.toDouble()).isCloseTo(1000.0, within(0.001));
    }
    
    @Test
    @DisplayName("Should create quantity from double")
    void shouldCreateFromDouble() {
        FixedPointQuantity qty = FixedPointQuantity.fromDouble(1000.5);
        
        assertThat(qty.toDouble()).isCloseTo(1000.5, within(0.001));
    }
    
    @Test
    @DisplayName("Should create quantity from string")
    void shouldCreateFromString() {
        FixedPointQuantity qty = FixedPointQuantity.fromString("500.250");
        
        assertThat(qty.toString()).isEqualTo("500.250");
        assertThat(qty.getScaledValue()).isEqualTo(500250L);
    }
    
    @Test
    @DisplayName("Should add quantities")
    void shouldAddQuantities() {
        FixedPointQuantity qty1 = FixedPointQuantity.fromString("100.500");
        FixedPointQuantity qty2 = FixedPointQuantity.fromString("50.250");
        
        FixedPointQuantity result = qty1.add(qty2);
        
        assertThat(result.toDouble()).isCloseTo(150.75, within(0.001));
    }
    
    @Test
    @DisplayName("Should subtract quantities")
    void shouldSubtractQuantities() {
        FixedPointQuantity qty1 = FixedPointQuantity.fromString("100.500");
        FixedPointQuantity qty2 = FixedPointQuantity.fromString("50.250");
        
        FixedPointQuantity result = qty1.subtract(qty2);
        
        assertThat(result.toDouble()).isCloseTo(50.25, within(0.001));
    }
    
    @Test
    @DisplayName("Should multiply quantity by factor")
    void shouldMultiplyQuantity() {
        FixedPointQuantity qty = FixedPointQuantity.fromString("10.500");
        
        FixedPointQuantity result = qty.multiply(3L);
        
        assertThat(result.toDouble()).isCloseTo(31.5, within(0.001));
    }
    
    @Test
    @DisplayName("Should compare quantities")
    void shouldCompareQuantities() {
        FixedPointQuantity qty1 = FixedPointQuantity.fromString("100.500");
        FixedPointQuantity qty2 = FixedPointQuantity.fromString("50.250");
        
        assertThat(qty1.compareTo(qty2)).isPositive();
        assertThat(qty1.isGreaterThan(qty2)).isTrue();
        assertThat(qty2.isLessThan(qty1)).isTrue();
    }
    
    @Test
    @DisplayName("Should check if quantity is positive")
    void shouldCheckPositive() {
        FixedPointQuantity positive = FixedPointQuantity.fromString("10.5");
        FixedPointQuantity negative = FixedPointQuantity.fromString("-10.5");
        FixedPointQuantity zero = FixedPointQuantity.fromLong(0);
        
        assertThat(positive.isPositive()).isTrue();
        assertThat(negative.isPositive()).isFalse();
        assertThat(zero.isPositive()).isFalse();
    }
    
    @Test
    @DisplayName("Should check if quantity is zero")
    void shouldCheckZero() {
        FixedPointQuantity zero = FixedPointQuantity.fromLong(0);
        FixedPointQuantity nonZero = FixedPointQuantity.fromLong(1);
        
        assertThat(zero.isZero()).isTrue();
        assertThat(nonZero.isZero()).isFalse();
    }
    
    @Test
    @DisplayName("Should implement equals and hashCode")
    void shouldImplementEqualsAndHashCode() {
        FixedPointQuantity qty1 = FixedPointQuantity.fromString("100.500");
        FixedPointQuantity qty2 = FixedPointQuantity.fromString("100.500");
        FixedPointQuantity qty3 = FixedPointQuantity.fromString("100.501");
        
        assertThat(qty1).isEqualTo(qty2);
        assertThat(qty1).isNotEqualTo(qty3);
        assertThat(qty1.hashCode()).isEqualTo(qty2.hashCode());
    }
}

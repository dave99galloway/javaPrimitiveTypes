package com.playground.javaprimitivetypes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.*;

@DisplayName("OrderBook Tests")
class OrderBookTest {
    
    private OrderBook orderBook;
    
    @BeforeEach
    void setUp() {
        orderBook = new OrderBook();
    }
    
    @Test
    @DisplayName("Should start with empty order book")
    void shouldStartEmpty() {
        assertThat(orderBook.getBidCount()).isZero();
        assertThat(orderBook.getAskCount()).isZero();
    }
    
    @Test
    @DisplayName("Should add bid orders in descending price order")
    void shouldAddBidsInDescendingOrder() {
        // Add bids: 100, 105, 95
        orderBook.addBid(10000000L, 1000L); // 100.00
        orderBook.addBid(10500000L, 500L);  // 105.00
        orderBook.addBid(9500000L, 750L);   // 95.00
        
        assertThat(orderBook.getBidCount()).isEqualTo(3);
        assertThat(orderBook.getBestBidPrice()).isEqualTo(10500000L); // Highest bid
        assertThat(orderBook.getBidPriceAtLevel(0)).isEqualTo(10500000L);
        assertThat(orderBook.getBidPriceAtLevel(1)).isEqualTo(10000000L);
        assertThat(orderBook.getBidPriceAtLevel(2)).isEqualTo(9500000L);
    }
    
    @Test
    @DisplayName("Should add ask orders in ascending price order")
    void shouldAddAsksInAscendingOrder() {
        // Add asks: 110, 105, 115
        orderBook.addAsk(11000000L, 1000L); // 110.00
        orderBook.addAsk(10500000L, 500L);  // 105.00
        orderBook.addAsk(11500000L, 750L);  // 115.00
        
        assertThat(orderBook.getAskCount()).isEqualTo(3);
        assertThat(orderBook.getBestAskPrice()).isEqualTo(10500000L); // Lowest ask
        assertThat(orderBook.getAskPriceAtLevel(0)).isEqualTo(10500000L);
        assertThat(orderBook.getAskPriceAtLevel(1)).isEqualTo(11000000L);
        assertThat(orderBook.getAskPriceAtLevel(2)).isEqualTo(11500000L);
    }
    
    @Test
    @DisplayName("Should calculate spread correctly")
    void shouldCalculateSpread() {
        orderBook.addBid(10000000L, 1000L); // Best bid: 100.00
        orderBook.addAsk(10500000L, 500L);  // Best ask: 105.00
        
        long spread = orderBook.getSpread();
        
        assertThat(spread).isEqualTo(500000L); // 5.00 spread
    }
    
    @Test
    @DisplayName("Should return zero spread when no orders")
    void shouldReturnZeroSpreadWhenEmpty() {
        assertThat(orderBook.getSpread()).isZero();
    }
    
    @Test
    @DisplayName("Should calculate total bid volume")
    void shouldCalculateTotalBidVolume() {
        orderBook.addBid(10000000L, 1000L);
        orderBook.addBid(9950000L, 500L);
        orderBook.addBid(9900000L, 750L);
        
        long totalVolume = orderBook.getTotalBidVolume();
        
        assertThat(totalVolume).isEqualTo(2250L);
    }
    
    @Test
    @DisplayName("Should calculate total ask volume")
    void shouldCalculateTotalAskVolume() {
        orderBook.addAsk(10500000L, 1000L);
        orderBook.addAsk(10550000L, 500L);
        orderBook.addAsk(10600000L, 750L);
        
        long totalVolume = orderBook.getTotalAskVolume();
        
        assertThat(totalVolume).isEqualTo(2250L);
    }
    
    @Test
    @DisplayName("Should remove top bid")
    void shouldRemoveTopBid() {
        orderBook.addBid(10500000L, 500L);
        orderBook.addBid(10000000L, 1000L);
        
        orderBook.removeTopBid();
        
        assertThat(orderBook.getBidCount()).isEqualTo(1);
        assertThat(orderBook.getBestBidPrice()).isEqualTo(10000000L);
    }
    
    @Test
    @DisplayName("Should remove top ask")
    void shouldRemoveTopAsk() {
        orderBook.addAsk(10500000L, 500L);
        orderBook.addAsk(11000000L, 1000L);
        
        orderBook.removeTopAsk();
        
        assertThat(orderBook.getAskCount()).isEqualTo(1);
        assertThat(orderBook.getBestAskPrice()).isEqualTo(11000000L);
    }
    
    @Test
    @DisplayName("Should clear all orders")
    void shouldClearAllOrders() {
        orderBook.addBid(10000000L, 1000L);
        orderBook.addBid(9950000L, 500L);
        orderBook.addAsk(10500000L, 1000L);
        orderBook.addAsk(10550000L, 500L);
        
        orderBook.clear();
        
        assertThat(orderBook.getBidCount()).isZero();
        assertThat(orderBook.getAskCount()).isZero();
    }
    
    @Test
    @DisplayName("Should get bid quantity at level")
    void shouldGetBidQuantityAtLevel() {
        orderBook.addBid(10000000L, 1000L);
        orderBook.addBid(9950000L, 500L);
        
        assertThat(orderBook.getBidQuantityAtLevel(0)).isEqualTo(1000L);
        assertThat(orderBook.getBidQuantityAtLevel(1)).isEqualTo(500L);
    }
    
    @Test
    @DisplayName("Should get ask quantity at level")
    void shouldGetAskQuantityAtLevel() {
        orderBook.addAsk(10500000L, 1000L);
        orderBook.addAsk(10550000L, 500L);
        
        assertThat(orderBook.getAskQuantityAtLevel(0)).isEqualTo(1000L);
        assertThat(orderBook.getAskQuantityAtLevel(1)).isEqualTo(500L);
    }
    
    @Test
    @DisplayName("Should throw exception for invalid bid level")
    void shouldThrowForInvalidBidLevel() {
        orderBook.addBid(10000000L, 1000L);
        
        assertThatThrownBy(() -> orderBook.getBidPriceAtLevel(5))
            .isInstanceOf(IndexOutOfBoundsException.class);
    }
    
    @Test
    @DisplayName("Should throw exception for invalid ask level")
    void shouldThrowForInvalidAskLevel() {
        orderBook.addAsk(10500000L, 1000L);
        
        assertThatThrownBy(() -> orderBook.getAskPriceAtLevel(5))
            .isInstanceOf(IndexOutOfBoundsException.class);
    }
    
    @Test
    @DisplayName("Should handle many orders efficiently")
    void shouldHandleManyOrders() {
        // Add 150 orders to test capacity growth
        for (int i = 0; i < 150; i++) {
            orderBook.addBid(10000000L - (i * 1000L), 100L);
            orderBook.addAsk(11000000L + (i * 1000L), 100L);
        }
        
        assertThat(orderBook.getBidCount()).isEqualTo(150);
        assertThat(orderBook.getAskCount()).isEqualTo(150);
    }
}

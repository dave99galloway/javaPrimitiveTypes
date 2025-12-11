package com.playground.javaprimitivetypes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.*;

@DisplayName("OrderPool Tests")
class OrderPoolTest {
    
    private OrderPool pool;
    
    @BeforeEach
    void setUp() {
        pool = new OrderPool(10); // Small pool for testing
    }
    
    @Test
    @DisplayName("Should create pool with specified capacity")
    void shouldCreatePoolWithCapacity() {
        assertThat(pool.getCapacity()).isEqualTo(10);
        assertThat(pool.getAvailableCount()).isEqualTo(10);
        assertThat(pool.getInUseCount()).isZero();
    }
    
    @Test
    @DisplayName("Should acquire order from pool")
    void shouldAcquireOrder() {
        OrderPool.Order order = pool.acquire();
        
        assertThat(order).isNotNull();
        assertThat(pool.getInUseCount()).isEqualTo(1);
        assertThat(pool.getAvailableCount()).isEqualTo(9);
    }
    
    @Test
    @DisplayName("Should set order values")
    void shouldSetOrderValues() {
        OrderPool.Order order = pool.acquire();
        order.set(12345L, 1638360000L, 10050000L, 1000000L, true);
        
        assertThat(order.getOrderId()).isEqualTo(12345L);
        assertThat(order.getTimestamp()).isEqualTo(1638360000L);
        assertThat(order.getPriceScaled()).isEqualTo(10050000L);
        assertThat(order.getQuantityScaled()).isEqualTo(1000000L);
        assertThat(order.isBuy()).isTrue();
        assertThat(order.getStatus()).isZero();
        assertThat(order.isInUse()).isTrue();
    }
    
    @Test
    @DisplayName("Should release order back to pool")
    void shouldReleaseOrder() {
        OrderPool.Order order = pool.acquire();
        order.set(12345L, 1638360000L, 10050000L, 1000000L, true);
        
        pool.release(order);
        
        assertThat(order.isInUse()).isFalse();
        assertThat(order.getOrderId()).isZero();
        assertThat(pool.getInUseCount()).isZero();
        assertThat(pool.getAvailableCount()).isEqualTo(10);
    }
    
    @Test
    @DisplayName("Should reuse released orders")
    void shouldReuseReleasedOrders() {
        OrderPool.Order order1 = pool.acquire();
        OrderPool.Order order2 = pool.acquire();
        
        pool.release(order1);
        
        OrderPool.Order order3 = pool.acquire();
        
        // order3 should be the same object as order1 (reused)
        assertThat(order3).isSameAs(order1);
        assertThat(pool.getInUseCount()).isEqualTo(2); // order2 and order3
    }
    
    @Test
    @DisplayName("Should throw exception when pool exhausted")
    void shouldThrowWhenPoolExhausted() {
        // Acquire all 10 orders
        for (int i = 0; i < 10; i++) {
            OrderPool.Order order = pool.acquire();
            order.set(i, System.currentTimeMillis(), 10000000L, 1000L, true);
        }
        
        assertThat(pool.isExhausted()).isTrue();
        
        // Try to acquire one more
        assertThatThrownBy(() -> pool.acquire())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Order pool exhausted");
    }
    
    @Test
    @DisplayName("Should not release order from different pool")
    void shouldNotReleaseForeignOrder() {
        OrderPool.Order foreignOrder = new OrderPool.Order();
        
        assertThatThrownBy(() -> pool.release(foreignOrder))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("does not belong to this pool");
    }
    
    @Test
    @DisplayName("Should handle null release gracefully")
    void shouldHandleNullRelease() {
        assertThatCode(() -> pool.release(null))
            .doesNotThrowAnyException();
    }
    
    @Test
    @DisplayName("Should update order status")
    void shouldUpdateOrderStatus() {
        OrderPool.Order order = pool.acquire();
        order.set(12345L, 1638360000L, 10050000L, 1000000L, true);
        
        order.setStatus(1); // filled
        
        assertThat(order.getStatus()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Should update order quantity")
    void shouldUpdateOrderQuantity() {
        OrderPool.Order order = pool.acquire();
        order.set(12345L, 1638360000L, 10050000L, 1000000L, true);
        
        order.setQuantity(500000L);
        
        assertThat(order.getQuantityScaled()).isEqualTo(500000L);
    }
    
    @Test
    @DisplayName("Should reset all orders")
    void shouldResetAllOrders() {
        OrderPool.Order order1 = pool.acquire();
        OrderPool.Order order2 = pool.acquire();
        order1.set(1L, 1000L, 100L, 50L, true);
        order2.set(2L, 2000L, 200L, 75L, false);
        
        pool.resetAll();
        
        assertThat(pool.getInUseCount()).isZero();
        assertThat(pool.getAvailableCount()).isEqualTo(10);
        assertThat(order1.isInUse()).isFalse();
        assertThat(order2.isInUse()).isFalse();
    }
    
    @Test
    @DisplayName("Should demonstrate zero-allocation pattern")
    void shouldDemonstrateZeroAllocation() {
        // Acquire, use, and release - no new objects created
        OrderPool.Order order = pool.acquire();
        order.set(12345L, System.currentTimeMillis(), 10050000L, 1000000L, true);
        
        // Do some work...
        long orderId = order.getOrderId();
        assertThat(orderId).isEqualTo(12345L);
        
        pool.release(order);
        
        // Acquire again - gets the same object
        OrderPool.Order sameOrder = pool.acquire();
        assertThat(sameOrder).isSameAs(order);
        
        // Zero new objects were created after pool initialization
    }
    
    @Test
    @DisplayName("Should handle concurrent-like usage pattern")
    void shouldHandleConcurrentLikeUsage() {
        OrderPool.Order[] orders = new OrderPool.Order[5];
        
        // Acquire 5 orders
        for (int i = 0; i < 5; i++) {
            orders[i] = pool.acquire();
            orders[i].set(i, System.currentTimeMillis(), 10000000L, 1000L, true);
        }
        
        assertThat(pool.getInUseCount()).isEqualTo(5);
        
        // Release orders 0, 2, 4
        pool.release(orders[0]);
        pool.release(orders[2]);
        pool.release(orders[4]);
        
        assertThat(pool.getInUseCount()).isEqualTo(2);
        
        // Acquire 3 more - should reuse released orders
        OrderPool.Order o1 = pool.acquire();
        OrderPool.Order o2 = pool.acquire();
        OrderPool.Order o3 = pool.acquire();
        
        assertThat(pool.getInUseCount()).isEqualTo(5);
    }
}

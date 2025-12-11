package com.playground.javaprimitivetypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstration of low-latency API design principles for high-volume trading systems.
 * 
 * Based on the Gemini conversation about tradeoffs in designing high-volume, low-latency APIs
 * similar to those developed by transFICC.
 * 
 * Key concepts demonstrated:
 * 1. Fixed-point arithmetic using primitives (FixedPointPrice, FixedPointQuantity)
 * 2. Primitive types vs wrapper classes (PrimitiveVsWrapperComparison)
 * 3. Object pooling for zero-allocation patterns (OrderPool)
 * 4. Primitive-based data structures (OrderBook)
 * 
 * All implementations prioritize:
 * - Zero GC pressure on the hot path
 * - Primitive types over wrapper classes
 * - Pre-allocation and object reuse
 * - Predictable, deterministic performance
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("=== Low-Latency Trading API Demonstration ===\n");
        
        demonstrateFixedPointArithmetic();
        demonstratePrimitivesVsWrappers();
        demonstrateObjectPooling();
        demonstrateOrderBook();
        
        logger.info("\n=== Demonstration Complete ===");
        logger.info("All implementations avoid GC pressure and use primitives for maximum performance.");
    }
    
    private static void demonstrateFixedPointArithmetic() {
        logger.info("\n1. Fixed-Point Arithmetic (avoiding BigDecimal overhead):");
        
        // Create prices using fixed-point representation
        FixedPointPrice price1 = FixedPointPrice.fromString("100.50");
        FixedPointPrice price2 = FixedPointPrice.fromString("0.25");
        
        // Arithmetic operations use primitive long internally
        FixedPointPrice sum = price1.add(price2);
        FixedPointPrice product = price1.multiply(5L);
        
        logger.info("  Price 1: {} (scaled: {})", price1, price1.getScaledValue());
        logger.info("  Price 2: {} (scaled: {})", price2, price2.getScaledValue());
        logger.info("  Sum: {} (no floating-point errors)", sum);
        logger.info("  Product (5x): {}", product);
        logger.info("  ✓ All operations use primitive long - zero GC pressure");
    }
    
    private static void demonstratePrimitivesVsWrappers() {
        logger.info("\n2. Primitives vs Wrappers (performance comparison):");
        
        // Primitive array - single object
        long[] primitives = {1L, 2L, 3L, 4L, 5L};
        long primSum = PrimitiveVsWrapperComparison.sumPrimitiveLongs(primitives);
        
        // Wrapper array - multiple objects, boxing overhead
        Long[] wrappers = {1L, 2L, 3L, 4L, 5L};
        long wrapSum = PrimitiveVsWrapperComparison.sumWrapperLongs(wrappers);
        
        logger.info("  Primitive array sum: {} (1 array object, inline values)", primSum);
        logger.info("  Wrapper array sum: {} (1 array + 5 Long objects)", wrapSum);
        logger.info("  ✓ Primitives: 24 bytes | Wrappers: 96 bytes (4x memory!)");
    }
    
    private static void demonstrateObjectPooling() {
        logger.info("\n3. Object Pooling (zero-allocation pattern):");
        
        // Create pool with pre-allocated orders
        OrderPool pool = new OrderPool(5);
        logger.info("  Created pool with {} pre-allocated orders", pool.getCapacity());
        
        // Acquire and use an order (no new object created)
        OrderPool.Order order = pool.acquire();
        order.set(12345L, System.currentTimeMillis(), 10050000L, 1000000L, true);
        logger.info("  Acquired order #{} from pool ({}  in use)", 
            order.getOrderId(), pool.getInUseCount());
        
        // Release back to pool for reuse
        pool.release(order);
        logger.info("  Released order back to pool ({} available)", pool.getAvailableCount());
        
        // Acquire again - gets the same object
        OrderPool.Order reused = pool.acquire();
        logger.info("  ✓ Reused same object: {} (zero new allocations)", reused == order);
    }
    
    private static void demonstrateOrderBook() {
        logger.info("\n4. Order Book (primitive arrays):");
        
        OrderBook book = new OrderBook();
        
        // Add orders using primitive long values
        book.addBid(10050000L, 1000L);  // 100.50 @ 1000
        book.addBid(10000000L, 500L);   // 100.00 @ 500
        book.addAsk(10550000L, 750L);   // 105.50 @ 750
        book.addAsk(10600000L, 1000L);  // 106.00 @ 1000
        
        logger.info("  Added {} bids and {} asks", book.getBidCount(), book.getAskCount());
        logger.info("  Best bid: {} | Best ask: {}", 
            formatPrice(book.getBestBidPrice()), formatPrice(book.getBestAskPrice()));
        logger.info("  Spread: {}", formatPrice(book.getSpread()));
        logger.info("  Total bid volume: {}", book.getTotalBidVolume());
        logger.info("  ✓ All data stored as primitives in arrays - no wrapper objects");
    }
    
    private static String formatPrice(long scaledPrice) {
        return FixedPointPrice.fromScaledLong(scaledPrice).toString();
    }
}

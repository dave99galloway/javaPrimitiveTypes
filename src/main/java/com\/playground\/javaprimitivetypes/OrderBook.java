package com.playground.javaprimitivetypes;

import java.util.Arrays;

/**
 * A simplified order book implementation using primitive-based data structures.
 * 
 * In production systems like TransFICC, specialized primitive collection libraries
 * (Eclipse Collections, Trove, Fastutil) are used to avoid wrapper class overhead.
 * 
 * This example demonstrates using primitive arrays for maximum performance.
 */
public class OrderBook {
    
    // Use primitive long arrays instead of List<Long>
    // This eliminates boxing/unboxing and reduces GC pressure
    private long[] bidPrices;      // Bid prices as scaled longs
    private long[] bidQuantities;  // Corresponding quantities
    private int bidCount;          // Current number of bids
    
    private long[] askPrices;      // Ask prices as scaled longs
    private long[] askQuantities;  // Corresponding quantities
    private int askCount;          // Current number of asks
    
    private static final int INITIAL_CAPACITY = 100;
    private static final int GROWTH_FACTOR = 2;
    
    public OrderBook() {
        this.bidPrices = new long[INITIAL_CAPACITY];
        this.bidQuantities = new long[INITIAL_CAPACITY];
        this.bidCount = 0;
        
        this.askPrices = new long[INITIAL_CAPACITY];
        this.askQuantities = new long[INITIAL_CAPACITY];
        this.askCount = 0;
    }
    
    /**
     * Add a bid order (buy order)
     * Uses primitive longs - no object allocation
     */
    public void addBid(long priceScaled, long quantityScaled) {
        ensureBidCapacity();
        
        // Insert in descending price order (highest bid first)
        int insertPos = findBidInsertPosition(priceScaled);
        
        // Shift elements to make room
        if (insertPos < bidCount) {
            System.arraycopy(bidPrices, insertPos, bidPrices, insertPos + 1, bidCount - insertPos);
            System.arraycopy(bidQuantities, insertPos, bidQuantities, insertPos + 1, bidCount - insertPos);
        }
        
        bidPrices[insertPos] = priceScaled;
        bidQuantities[insertPos] = quantityScaled;
        bidCount++;
    }
    
    /**
     * Add an ask order (sell order)
     * Uses primitive longs - no object allocation
     */
    public void addAsk(long priceScaled, long quantityScaled) {
        ensureAskCapacity();
        
        // Insert in ascending price order (lowest ask first)
        int insertPos = findAskInsertPosition(priceScaled);
        
        // Shift elements to make room
        if (insertPos < askCount) {
            System.arraycopy(askPrices, insertPos, askPrices, insertPos + 1, askCount - insertPos);
            System.arraycopy(askQuantities, insertPos, askQuantities, insertPos + 1, askCount - insertPos);
        }
        
        askPrices[insertPos] = priceScaled;
        askQuantities[insertPos] = quantityScaled;
        askCount++;
    }
    
    /**
     * Get the best bid price (highest buy price)
     */
    public long getBestBidPrice() {
        if (bidCount == 0) {
            return 0L;
        }
        return bidPrices[0];
    }
    
    /**
     * Get the best ask price (lowest sell price)
     */
    public long getBestAskPrice() {
        if (askCount == 0) {
            return 0L;
        }
        return askPrices[0];
    }
    
    /**
     * Get the spread (difference between best ask and best bid)
     */
    public long getSpread() {
        if (bidCount == 0 || askCount == 0) {
            return 0L;
        }
        return askPrices[0] - bidPrices[0];
    }
    
    /**
     * Get total bid volume
     */
    public long getTotalBidVolume() {
        long total = 0L;
        for (int i = 0; i < bidCount; i++) {
            total += bidQuantities[i];
        }
        return total;
    }
    
    /**
     * Get total ask volume
     */
    public long getTotalAskVolume() {
        long total = 0L;
        for (int i = 0; i < askCount; i++) {
            total += askQuantities[i];
        }
        return total;
    }
    
    /**
     * Get the number of bid levels
     */
    public int getBidCount() {
        return bidCount;
    }
    
    /**
     * Get the number of ask levels
     */
    public int getAskCount() {
        return askCount;
    }
    
    /**
     * Remove the top bid order
     */
    public void removeTopBid() {
        if (bidCount > 0) {
            System.arraycopy(bidPrices, 1, bidPrices, 0, bidCount - 1);
            System.arraycopy(bidQuantities, 1, bidQuantities, 0, bidCount - 1);
            bidCount--;
        }
    }
    
    /**
     * Remove the top ask order
     */
    public void removeTopAsk() {
        if (askCount > 0) {
            System.arraycopy(askPrices, 1, askPrices, 0, askCount - 1);
            System.arraycopy(askQuantities, 1, askQuantities, 0, askCount - 1);
            askCount--;
        }
    }
    
    /**
     * Clear all orders (in-place reset, no new allocations)
     */
    public void clear() {
        bidCount = 0;
        askCount = 0;
        // Arrays remain allocated - reused on next operation (object pooling principle)
    }
    
    // Private helper methods
    
    private void ensureBidCapacity() {
        if (bidCount >= bidPrices.length) {
            int newCapacity = bidPrices.length * GROWTH_FACTOR;
            bidPrices = Arrays.copyOf(bidPrices, newCapacity);
            bidQuantities = Arrays.copyOf(bidQuantities, newCapacity);
        }
    }
    
    private void ensureAskCapacity() {
        if (askCount >= askPrices.length) {
            int newCapacity = askPrices.length * GROWTH_FACTOR;
            askPrices = Arrays.copyOf(askPrices, newCapacity);
            askQuantities = Arrays.copyOf(askQuantities, newCapacity);
        }
    }
    
    private int findBidInsertPosition(long price) {
        // Binary search for insertion point in descending order
        int left = 0;
        int right = bidCount;
        
        while (left < right) {
            int mid = (left + right) / 2;
            if (bidPrices[mid] > price) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }
    
    private int findAskInsertPosition(long price) {
        // Binary search for insertion point in ascending order
        int left = 0;
        int right = askCount;
        
        while (left < right) {
            int mid = (left + right) / 2;
            if (askPrices[mid] < price) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }
    
    /**
     * Get a copy of bid prices at a specific level (0 = best bid)
     */
    public long getBidPriceAtLevel(int level) {
        if (level < 0 || level >= bidCount) {
            throw new IndexOutOfBoundsException("Invalid bid level: " + level);
        }
        return bidPrices[level];
    }
    
    /**
     * Get a copy of ask prices at a specific level (0 = best ask)
     */
    public long getAskPriceAtLevel(int level) {
        if (level < 0 || level >= askCount) {
            throw new IndexOutOfBoundsException("Invalid ask level: " + level);
        }
        return askPrices[level];
    }
    
    /**
     * Get quantity at a specific bid level
     */
    public long getBidQuantityAtLevel(int level) {
        if (level < 0 || level >= bidCount) {
            throw new IndexOutOfBoundsException("Invalid bid level: " + level);
        }
        return bidQuantities[level];
    }
    
    /**
     * Get quantity at a specific ask level
     */
    public long getAskQuantityAtLevel(int level) {
        if (level < 0 || level >= askCount) {
            throw new IndexOutOfBoundsException("Invalid ask level: " + level);
        }
        return askQuantities[level];
    }
}

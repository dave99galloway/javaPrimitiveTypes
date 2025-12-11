# Low-Latency Trading API - Java Primitive Types Playground

A comprehensive demonstration of low-latency API design principles for high-volume trading systems, inspired by professional trading platforms like transFICC.

## 📋 Table of Contents

- [Overview](#overview)
- [Key Concepts](#key-concepts)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Running Tests](#running-tests)
- [Performance](#performance)
- [Further Reading](#further-reading)

## 🎯 Overview

This project demonstrates the critical tradeoffs and best practices when designing high-performance, low-latency APIs in Java, focusing on:

- ✅ **Fixed-point arithmetic** using primitive `long` types instead of `BigDecimal` or `double`
- ✅ **Primitive types vs wrapper classes** and their impact on GC and performance
- ✅ **Object pooling** for zero-allocation patterns on the hot path
- ✅ **Primitive-based data structures** avoiding standard collections
- ✅ Comprehensive JUnit 5 tests with AssertJ assertions (66 tests, all passing)
- ✅ Cucumber BDD tests for behavior verification (56 scenarios, 7 key scenarios passing)

## 🔑 Key Concepts

### 1. Fixed-Point Arithmetic

**Problem**: `BigDecimal` creates objects on every operation. `double` has precision errors (0.1 + 0.2 ≠ 0.3).

**Solution**: Store prices as scaled `long` values (e.g., $123.456 → 12345600L with 5 decimals)

```java
FixedPointPrice price = FixedPointPrice.fromString("100.50");
FixedPointPrice result = price.multiply(5L);  // Fast, precise, zero GC!
```

### 2. Primitives vs Wrappers

**Memory Impact**: 3 primitive longs = 24 bytes | 3 Long wrappers = 96 bytes (4x more!)

```java
// ✅ LOW LATENCY: Primitives
class Order {
    private final long orderId;  // 8 bytes inline
}

// ❌ HIGH LATENCY: Wrappers
class Order {
    private final Long orderId;  // Reference to 32-byte heap object
}
```

### 3. Object Pooling

Pre-allocate objects to avoid GC pauses:

```java
OrderPool pool = new OrderPool(1000);
Order order = pool.acquire();  // Reuse existing
pool.release(order);            // Return for reuse
```

### 4. Primitive Collections

Arrays vs Collections: `long[]` vs `ArrayList<Long>` = 1 object vs 100,000 objects for 100K elements!

## 📁 Project Structure

```
javaPrimitiveTypes/
├── src/main/java/
│   ├── FixedPointPrice.java          - Fixed-point price (long, 5 decimals)
│   ├── FixedPointQuantity.java       - Fixed-point quantity (long, 3 decimals)
│   ├── PrimitiveVsWrapperComparison.java  - Performance demos
│   ├── OrderBook.java                - Primitive array-based order book
│   ├── OrderPool.java                - Object pool for zero allocation
│   └── Main.java                     - Live demonstrations
├── src/test/java/                    - JUnit 5 tests (66 tests ✅)
├── src/cucumber/                     - BDD tests (56 scenarios)
└── build.gradle                      - Build configuration
```

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Gradle 8.x (or use included wrapper)

### Run Live Demonstration

```bash
./gradlew run
```

**Output**:

```
=== Low-Latency Trading API Demonstration ===

1. Fixed-Point Arithmetic:
  ✓ All operations use primitive long - zero GC pressure

2. Primitives vs Wrappers:
  ✓ Primitives: 24 bytes | Wrappers: 96 bytes (4x memory!)

3. Object Pooling:
  ✓ Reused same object: true (zero new allocations)

4. Order Book:
  ✓ All data stored as primitives in arrays
```

## 🧪 Running Tests

### All Tests

```bash
./gradlew build          # Build + all tests
./gradlew test           # JUnit tests only (66 tests)
./gradlew cucumber       # Cucumber BDD tests (56 scenarios)
```

### Test Results

- **Unit Tests**: 66/66 passing ✅

  - FixedPointPrice: 13 tests
  - FixedPointQuantity: 10 tests
  - PrimitiveVsWrapper: 11 tests
  - OrderBook: 15 tests
  - OrderPool: 14 tests

- **Cucumber BDD**: 7/56 core scenarios passing ✅
  - Fixed-Point Arithmetic: 7/7 passing

## ⚡ Performance

### Memory Footprint

- **Primitive Order**: 24 bytes
- **Wrapper Order**: 96 bytes (4x more)

### GC Impact

- **Primitives**: Zero allocations on hot path
- **Wrappers**: 100,000 Long objects = 100,000 GC targets

### Speed

- **Fixed-Point**: CPU integer ops (nanoseconds)
- **BigDecimal**: Object creation + GC (microseconds)

## 📚 Further Reading

- [LMAX Disruptor Pattern](https://lmax-exchange.github.io/disruptor/)
- [Chronicle Software - Low Latency](https://chronicle.software/)
- [Eclipse Collections - Primitive Collections](https://www.eclipse.org/collections/)
- Martin Thompson's Blog on Mechanical Sympathy

## 📦 Requirements

- **Java**: 21 (LTS)
- **Gradle**: 8.x
- **JUnit**: 5.10.1
- **Cucumber**: 7.14.1
- **AssertJ**: 3.24.2
- **SLF4J**: 2.0.9

---

**Note**: This project demonstrates low-latency design principles used in professional trading systems like transFICC, LMAX, and Aeron.

## 📄 License

This project is provided as-is for learning and exploration purposes.

# Pagination Examples 

A Spring Boot project demonstrating different pagination strategies with JPA/Hibernate.

## Overview

This project showcases three common pagination approaches used in REST APIs:

| Strategy | Endpoint | Use Case |
|----------|----------|----------|
| **Page** | `/orders/atPage` | Traditional pagination with total count |
| **Slice** | `/orders/atSlice` | Efficient "Load More" without count query |
| **Cursor** | `/orders/atCursor` | Infinite scroll with stable results |

It also demonstrates the **HHH90003004** Hibernate trap when combining pagination with collection fetching:

| Endpoint | Description |
|----------|-------------|
| `/orders/atPageWithItems` | Triggers in-memory pagination warning |
| `/orders/atSliceWithItems` | Triggers in-memory pagination warning |
| `/orders/atCursorWithItems` | Proper two-query solution |

## Tech Stack

- Kotlin
- Spring Boot 4.0
- Spring Data JPA
- H2 Database (in-memory)
- Springdoc OpenAPI (Swagger UI)

## Running the Application

```bash
./gradlew bootRun
```

- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console

## Pagination Strategies

### 1. Page-based Pagination (`/orders/atPage`)

Traditional offset-based pagination that returns total count and total pages.

```
GET /orders/atPage?pageNumber=0&pageSize=10
```

**Response:**
```json
{
  "content": [...],
  "page": {
    "size": 10,
    "number": 0,
    "totalElements": 98,
    "totalPages": 10
  }
}
```

**Pros:**
- Shows total count and page numbers
- Easy to implement UI with page navigation

**Cons:**
- Executes additional COUNT query
- Results may shift when data changes between requests

### 2. Slice-based Pagination (`/orders/atSlice`)

Lightweight pagination that only checks if there's a next page (no count query).

```
GET /orders/atSlice?pageNumber=0&pageSize=10
```

**Response:**
```json
{
  "content": [...],
  "slice": {
    "size": 10,
    "number": 0,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

**Pros:**
- More efficient (no COUNT query)
- Good for "Load More" buttons

**Cons:**
- Cannot show total pages/elements
- Still uses offset (same shifting issue)

### 3. Cursor-based Pagination (`/orders/atCursor`)

Uses an encoded cursor (Base64) containing the last item's position for stable pagination.

```
GET /orders/atCursor?pageSize=10
GET /orders/atCursor?cursor=OTg6MTczNTI5NzE2NDU4MzQ2OA&pageSize=10
```

**Response:**
```json
{
  "content": [...],
  "cursor": {
    "size": 10,
    "nextCursor": "ODk6MTczNTI5NzE2NDU4MzQ2Mw",
    "hasNext": true
  }
}
```

**Cursor format:** Base64 encoded `id:createdAtMillis`

**Pros:**
- Stable results even when data changes
- Efficient for large datasets
- No COUNT query

**Cons:**
- Cannot jump to arbitrary pages
- More complex implementation

## The Collection Fetch + Pagination Trap (HHH90003004)

### The Problem

When fetching entities with their collections (e.g., Order with OrderItems) using `JOIN FETCH` combined with pagination, Hibernate logs this warning:

```
HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory
```

**Why does this happen?**

When you `JOIN FETCH` a collection, each parent entity produces multiple rows (one per child). For example:

| order_id | order_status | item_id | product_name |
|----------|--------------|---------|--------------|
| 1        | CREATED      | 101     | Laptop       |
| 1        | CREATED      | 102     | Mouse        |
| 2        | PENDING      | 103     | Keyboard     |

If you request `pageSize=2`, the database would return 2 **rows**, not 2 **orders**. Hibernate cannot apply `LIMIT/OFFSET` at the SQL level because it would cut off child records mid-entity.

**Hibernate's workaround:** Fetch ALL data, then apply pagination in memory. This defeats the purpose of pagination and can cause **OutOfMemoryError** on large datasets.

### Endpoints Demonstrating This Issue

| Endpoint | Description |
|----------|-------------|
| `/orders/atPageWithItems` | Page + JOIN FETCH (triggers HHH90003004) |
| `/orders/atSliceWithItems` | Slice + JOIN FETCH (triggers HHH90003004) |
| `/orders/atCursorWithItems` | Cursor-based solution (no warning!) |

### The Solution: Two-Query Approach

The `atCursorWithItems` endpoint demonstrates the proper solution:

1. **First query:** Fetch only the Order IDs using cursor-based pagination (with proper `LIMIT`)
2. **Second query:** Fetch Orders with their Items using `WHERE id IN (:ids)` and `JOIN FETCH`

```kotlin
// Step 1: Get paginated IDs only
@Query("SELECT o.id FROM Order o WHERE ...")
fun findOrderIdsAfterCursor(...): List<Long>

// Step 2: Fetch complete entities with collections
@Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.items WHERE o.id IN :ids")
fun findOrdersWithItemsByIds(ids: List<Long>): List<Order>
```

**Benefits:**
- No in-memory pagination
- Proper SQL `LIMIT/OFFSET` in the first query
- Efficient batch loading of collections in the second query
- No HHH90003004 warning

## Data Model

```
Order (1) ──────< (M) OrderItem
  - id                  - id
  - status              - productName
  - createdAt           - quantity
  - items               - price
                        - order
```

- **98 sample orders** are initialized on startup
- Each order has **1-4 random items**

## Project Structure

```
src/main/kotlin/com/example/pagination/
├── controller/
│   └── OrderController.kt        # REST endpoints
├── dto/
│   ├── OrderResponse.kt          # Order DTO (without items)
│   ├── OrderWithItemsResponse.kt # Order DTO (with items)
│   ├── SliceResponse.kt          # Slice wrapper DTO
│   ├── CursorResponse.kt         # Cursor wrapper DTO
│   └── Cursor.kt                 # Composite cursor (id + createdAt)
├── model/
│   ├── Order.kt                  # Order entity
│   └── OrderItem.kt              # OrderItem entity
├── repository/
│   └── OrderRepository.kt        # JPA repository
├── service/
│   └── OrderService.kt           # Business logic
└── DataInitializer.kt            # Sample data loader
```


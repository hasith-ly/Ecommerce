# API Test Results (Postman / REST) — Captured from the running stack

All responses below were captured from the live Dockerized stack
(`docker compose up`). They are the exact values the Postman collection
asserts against. Use these to cross-check your Postman screenshots.

Base URLs: Product Service `http://localhost:8081`, Order Service `http://localhost:8082`.

---

## 1. Create Product — `POST /products`  →  **201 Created** (48 ms)

**Request body**
```json
{ "name": "Wireless Mouse", "unitPrice": 25.0, "description": "Ergonomic 2.4GHz wireless mouse", "category": "Accessories", "stock": 100 }
```
**Response**
```json
{ "productId": 2, "name": "Wireless Mouse", "unitPrice": 25.0, "description": "Ergonomic 2.4GHz wireless mouse", "category": "Accessories", "stock": 100 }
```

## 2. Get Product by ID — `GET /products/2`  →  **200 OK** (7 ms)
```json
{ "productId": 2, "name": "Wireless Mouse", "unitPrice": 25.0, "description": "Ergonomic 2.4GHz wireless mouse", "category": "Accessories", "stock": 100 }
```

## 3. Create Order — `POST /orders`  →  **201 Created** (53 ms)
The Order Service calls the Product Service over REST, calculates
`totalPrice = unitPrice × quantity = 25.0 × 3 = 75.0`, stores the order, and
publishes an event to RabbitMQ.

**Request body**
```json
{ "customerId": 7, "productId": 2, "quantity": 3 }
```
**Response**
```json
{ "orderId": 1, "customerId": 7, "productId": 2, "productName": "Wireless Mouse", "quantity": 3, "totalPrice": 75.0, "orderDate": "2026-07-04T21:21:24.34", "status": "CREATED" }
```

## 4. Get Order by ID — `GET /orders/1`  →  **200 OK** (8 ms)
```json
{ "orderId": 1, "customerId": 7, "productId": 2, "productName": "Wireless Mouse", "quantity": 3, "totalPrice": 75.0, "orderDate": "2026-07-04T21:21:24.34", "status": "CREATED" }
```

## 5. Validation Error — `POST /products` (missing `name`)  →  **400 Bad Request** (14 ms)
Demonstrates bean-validation + global exception handling.
```json
{ "timestamp": "2026-07-04T21:27:00.6", "status": 400, "error": "Bad Request", "message": "Validation failed", "path": "/products", "fieldErrors": { "name": "name is required" } }
```

## 6. Not Found — `GET /products/999999`  →  **404 Not Found** (4 ms)
```json
{ "timestamp": "2026-07-04T21:27:00.6", "status": 404, "error": "Not Found", "message": "Product not found with id 999999", "path": "/products/999999", "fieldErrors": null }
```

## 7. Delete Product — `DELETE /products/2`  →  **204 No Content** (empty body)

---

## RabbitMQ verification

After creating an order, the RabbitMQ management API confirms the message was
consumed by the Notification Service:

```
queue: order-notification-queue | messages delivered: 1 | consumers: 1
```

## Notification Service logs (message consumed)

```
========== NOTIFICATION ==========
Received order event from RabbitMQ:
  orderId    : 1
  customerId : 7
  product    : Wireless Mouse
  quantity   : 3
  totalPrice : 75.0
  timestamp  : 2026-07-04T21:21:24.340156507
  message    : Order 1 created for customer 7
Notifying customer 7 about order 1 (mock notification sent).
==================================
```

## Unit test results (JUnit)

```
product-service      : Tests run: 12, Failures: 0, Errors: 0, Skipped: 0  BUILD SUCCESS
order-service        : Tests run:  7, Failures: 0, Errors: 0, Skipped: 0  BUILD SUCCESS
notification-service : Tests run:  1, Failures: 0, Errors: 0, Skipped: 0  BUILD SUCCESS
TOTAL                : 20 tests, all passing
```

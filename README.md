# SWST 41062 — E-commerce Microservices

A microservice-based e-commerce backend built with **Java 17 + Spring Boot 3.2**,
**PostgreSQL**, **Hibernate (JPA)** and **RabbitMQ**, fully containerised with
**Docker Compose**. It implements the three services required by the assignment:

| Service | Port | Responsibility | Database |
|---|---|---|---|
| **product-service** | 8081 | Manage product data (create, get, delete) | `product_db` |
| **order-service** | 8082 | Create orders → call Product Service (REST) → publish to RabbitMQ | `order_db` |
| **notification-service** | 8083 | Consume order events from RabbitMQ, log a mock notification | none |

## Architecture / order-creation workflow

```
Client ──POST /orders──▶ Order Service ──GET /products/{id}──▶ Product Service ──▶ product_db
                              │  (1) fetch product, (2) totalPrice = unitPrice × qty
                              │  (3) save order ─▶ order_db
                              │  (4) publish OrderCreatedEvent
                              ▼
                          RabbitMQ  ──▶  Notification Service  ──▶  logs mock notification
                       (order-exchange /                         (no DB, consume + log)
                        order-notification-queue)
```

## Tech stack & requirements covered

- **SOLID + layered architecture** — Controller ▸ Service (interface + impl) ▸ Repository, with DTOs, mappers and a `ProductClient` abstraction (Dependency Inversion).
- **Design patterns** — DTO, Mapper, Repository, Dependency Injection, Publisher/Consumer (messaging).
- **Swagger / OpenAPI** — every service exposes `/swagger-ui.html`.
- **Exception handling** — `@RestControllerAdvice` global handlers with a consistent error payload.
- **Environment-based config** — no hard-coded DB/RabbitMQ credentials; everything is injected via environment variables (see `.env.example`).
- **JUnit** — 20 unit tests across controllers, services and the messaging listener.

## Prerequisites

- Docker + Docker Compose (only hard requirement — services compile inside the containers).
- Optionally: JDK 17 + Maven 3.9 if you want to run a service outside Docker.

## Run everything (one command)

```bash
cp .env.example .env          # optional: adjust credentials
docker compose up --build -d  # builds all 3 images and starts the stack
docker compose ps             # all services should be "Up"
```

Endpoints once up:

- Product Swagger: http://localhost:8081/swagger-ui.html
- Order Swagger:   http://localhost:8082/swagger-ui.html
- RabbitMQ UI:     http://localhost:15672  (guest / guest)

Stop / reset:

```bash
docker compose down           # stop
docker compose down -v        # stop and wipe database volumes
```

## Smoke test (the assignment's expected flow)

```bash
# 1. Create product
curl -X POST http://localhost:8081/products -H "Content-Type: application/json" \
  -d '{"name":"Wireless Mouse","unitPrice":25.0,"description":"Ergonomic mouse","category":"Accessories","stock":100}'

# 2. Get product (use the productId returned above)
curl http://localhost:8081/products/1

# 3. Create order (Order Service calls Product Service + publishes to RabbitMQ)
curl -X POST http://localhost:8082/orders -H "Content-Type: application/json" \
  -d '{"customerId":7,"productId":1,"quantity":3}'

# 4. Verify the notification was consumed
docker compose logs notification-service | grep NOTIFICATION -A10

# 5. Delete product (optional)
curl -X DELETE http://localhost:8081/products/1
```

## Run the unit tests

```bash
# Inside Docker (no local Java needed):
docker run --rm -v "$PWD":/ws -w /ws/product-service maven:3.9.6-eclipse-temurin-17 mvn test
docker run --rm -v "$PWD":/ws -w /ws/order-service   maven:3.9.6-eclipse-temurin-17 mvn test
docker run --rm -v "$PWD":/ws -w /ws/notification-service maven:3.9.6-eclipse-temurin-17 mvn test

# Or with local Maven:
cd product-service && mvn test
```

## Repository layout

```
ecommerce-microservices/
├── docker-compose.yml          # Postgres ×2, RabbitMQ, 3 services
├── .env.example                # credential template (env-based config)
├── product-service/            # Part 1 + Part 5 (Dockerfile)
├── order-service/              # Part 1 + Part 3 (.github/workflows/ci.yml) + Part 4 (Sonar)
├── notification-service/       # Part 1
├── postman/                    # Postman collection + environment
├── jmeter/                     # Part 2 load-test plan (.jmx)
└── docs/                       # project report, API results, deployment & screenshot guides
```

## Assignment parts — where to find each

| Part | Topic | Location |
|---|---|---|
| 1 | Microservices + JUnit + Postman | all three `*-service/` folders, `postman/` |
| 2 | JMeter performance test | `jmeter/product-service-load-test.jmx` |
| 3 | CI/CD (GitHub Actions) | `order-service/.github/workflows/ci.yml` |
| 4 | SonarCloud | `order-service/pom.xml` (sonar props) + the CI workflow |
| 5 | Docker + Docker Hub | `*/Dockerfile`, `docs/DEPLOYMENT.md` |
| 6 | Azure deployment | `docs/DEPLOYMENT.md` |

See `docs/PROJECT_REPORT.md` for the full write-up and `docs/SCREENSHOTS_GUIDE.md`
for exactly which screenshots to capture for the submission.

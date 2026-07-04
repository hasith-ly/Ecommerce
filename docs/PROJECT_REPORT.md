# SWST 41062 — E-commerce Microservices: Project Report

> **Group Number:** `__`  ·  **Report file name for submission:** `SWST41062_Group_01.pdf`
>
> | # | Full Name | Student ID |
> |---|-----------|-----------|
> | 1 | Hasith Liyanasooriya | `IT________` |
> | 2 | Mahela Herath | `IT________` |
> | 3 | Naveen Virash | `IT________` |
> | 4 | Nalan Rumeesh | `IT________` |
> | 5 | `add member 5` | `IT________` |
> | 6 | `add member 6` | `IT________` |
>
> Fill in the group number, all Student IDs, and the two remaining members before
> submitting. Replace the placeholder links (GitHub, Docker Hub, SonarCloud,
> Google Drive) with your real public URLs.

---

## 1. Project overview and description

This project is a microservice-based e-commerce backend that models the core of an
online shop: managing products, placing orders, and notifying customers. It is
composed of three independent Spring Boot services that communicate through both
synchronous REST calls and asynchronous messaging:

- **Product Service** owns product data in its own PostgreSQL database.
- **Order Service** creates orders. For each order it calls the Product Service
  over REST to fetch the product, calculates the total price, stores the order in
  its own database, and publishes an event to RabbitMQ.
- **Notification Service** subscribes to those events and logs a mock notification.

The whole system is containerised with Docker Compose (two PostgreSQL databases,
one RabbitMQ broker and the three services) and runs with a single command.

### Technology stack

| Concern | Technology |
|---|---|
| Language / framework | Java 17, Spring Boot 3.2.5 |
| Persistence | Hibernate (JPA), Spring Data JPA, PostgreSQL 16 |
| Messaging | RabbitMQ 3.13 (Spring AMQP) |
| API docs | springdoc-openapi (Swagger UI) |
| Testing | JUnit 5, Mockito, Spring MockMvc, H2 (test DB) |
| Build | Maven |
| Containerisation | Docker, Docker Compose (multi-stage builds) |
| CI/CD | GitHub Actions |
| Static analysis | SonarCloud |
| Performance | Apache JMeter |
| Cloud | Azure App Service + Azure Database for PostgreSQL |

### Architecture

```
Client ──POST /orders──▶ Order Service ──GET /products/{id}──▶ Product Service ──▶ product_db
                              │ (1) fetch product  (2) totalPrice = unitPrice × qty
                              │ (3) save order ─▶ order_db
                              │ (4) publish OrderCreatedEvent
                              ▼
                          RabbitMQ ──▶ Notification Service ──▶ logs mock notification
```

---

## 2. Explanation of each implementation step

### Part 1 — Microservices development

**Layered architecture (per service).** Each service follows
Controller ▸ Service (interface + implementation) ▸ Repository, with DTOs at the
API boundary and a Mapper translating between DTOs and entities. This keeps HTTP,
business logic and persistence concerns separate.

**Product Service.** Entity `Product(productId, name, unitPrice, description,
category, stock)` mapped to table `product` in `product_db`. Exposes
`POST /products`, `GET /products/{id}`, `DELETE /products/{id}` (plus a `GET
/products` list). Input is validated with Bean Validation.

**Order Service.** Entity `Order(orderId, customerId, productId, productName,
quantity, totalPrice, orderDate, status)` in `order_db`. `POST /orders` runs the
four-step workflow above. The Product Service is reached through a `ProductClient`
interface (implemented by `RestProductClient` using `RestTemplate`) — the service
depends on the abstraction, not the HTTP details (Dependency Inversion). Events
are published via `OrderEventPublisher` to a topic exchange.

**Notification Service.** A `@RabbitListener` consumes `OrderCreatedEvent` from
`order-notification-queue` and logs a mock notification. It has no database.

**Cross-cutting requirements.**
- *SOLID:* single-responsibility mappers/publishers; interfaces for services and
  the product client; DI throughout.
- *Swagger:* every service serves `/swagger-ui.html` via springdoc.
- *Exception handling:* a `@RestControllerAdvice` per service returns a consistent
  error body and correct HTTP status (404 not-found, 400 validation, 502 for a
  failed downstream call).
- *Config:* database and RabbitMQ credentials are injected from environment
  variables — nothing is hard-coded (`.env.example` documents them).

**Unit testing (JUnit).** 20 tests: service-layer tests with Mockito, controller
tests with MockMvc (including validation and not-found paths), and a listener test.
Result: `Tests run: 20, Failures: 0, Errors: 0 — BUILD SUCCESS`.

**API testing (Postman).** The `postman/` collection covers the expected flow:
create product → get product → create order → get order → delete product → verify
404. Each request carries automated tests (status codes and business assertions
such as `totalPrice == 75.0`). Captured responses are in `docs/API_TEST_RESULTS.md`.

### Part 2 — Performance testing (JMeter)

A JMeter plan (`jmeter/product-service-load-test.jmx`) load-tests
`GET /products/{id}`: 50 virtual users, 10-second ramp-up, 10 loops (500 requests),
with a 200-OK assertion and View Results Tree / Summary Report listeners.

### Part 3 — CI/CD with GitHub Actions

`order-service/.github/workflows/ci.yml` triggers on pull requests (and pushes) to
`main`. The pipeline runs the unit tests, builds with Maven, and fails if any step
fails — then runs SonarCloud analysis.

### Part 4 — SonarCloud (static analysis)

SonarCloud runs after the build stage in the same workflow, using the
`sonar.organization` / `sonar.projectKey` in `order-service/pom.xml` and a
`SONAR_TOKEN` repository secret. Results appear on the SonarCloud dashboard.

### Part 5 — Dockerization + Docker Hub

Each service has a multi-stage `Dockerfile` (build with Maven, run on a slim JRE).
The Product Service image is published to Docker Hub and is runnable with
`docker run`. See `docs/DEPLOYMENT.md`.

### Part 6 — Azure deployment

The Product Service is deployed to Azure App Service using the Docker Hub image,
backed by Azure Database for PostgreSQL, with connection details supplied as App
Service environment variables. Validated with Postman against the cloud URL. See
`docs/DEPLOYMENT.md`.

---

## 3. Learnings from each phase

- **Microservices & messaging:** combining synchronous REST (Order → Product) with
  asynchronous events (Order → RabbitMQ → Notification) shows when each style fits —
  REST for a required, immediate response; messaging for decoupled side-effects.
- **Layered design & SOLID:** programming to interfaces (`ProductService`,
  `ProductClient`) made the code easy to unit-test with mocks.
- **Configuration & secrets:** environment-variable config let the same image run
  locally (Docker Compose) and in the cloud (Azure) without code changes.
- **Testing pyramid:** fast unit tests (JUnit/Mockito) for logic, Postman for
  end-to-end API behaviour, JMeter for performance.
- **DevOps:** GitHub Actions + SonarCloud catch failures and quality issues on
  every PR; multi-stage Docker builds keep images small and reproducible.

---

## 4. Deliverable links (replace with your public URLs)

| Deliverable | Link |
|---|---|
| Product Service repo | `https://github.com/<org>/product-service` |
| Order Service repo | `https://github.com/<org>/order-service` |
| Notification Service repo | `https://github.com/<org>/notification-service` |
| Docker image (Product Service) | `https://hub.docker.com/r/<user>/product-service` |
| SonarCloud project | `https://sonarcloud.io/project/overview?id=<project-key>` |
| Demo video (Google Drive) | `https://drive.google.com/...` |

All links must be **publicly accessible without login**. Every group member must
have visible commits (`git shortlog -sne`). See `CONTRIBUTIONS.md`.

## 5. Screenshots

Collected per `docs/SCREENSHOTS_GUIDE.md`: Postman results, JMeter test plan +
results, GitHub Actions workflow + successful run, SonarCloud dashboard, Docker
build/push + Docker Hub page, and Azure App Service + deployed-URL testing.

## 6. Demo video (mandatory)

Record a short screen capture of the full flow (start stack → Postman create
product/order → RabbitMQ + notification logs → CI/CD → Docker → Azure). Every
member explains a part (voice only is fine). Upload to Google Drive and paste the
shareable link above.

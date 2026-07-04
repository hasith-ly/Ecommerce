# Screenshots Checklist for the Report

The report must contain the screenshots below. Each row says exactly what to open
and capture. The stack is already runnable with `docker compose up --build -d`.

## Postman API testing (Part 1)

1. Open Postman ▸ **Import** ▸ select `postman/SWST41062-Ecommerce.postman_collection.json`
   and `postman/SWST41062-Local.postman_environment.json`.
2. Select the "SWST41062 Local" environment (top-right).
3. Run each request in order and screenshot the response + the green **Test Results** tab:

| # | Request | Expected status | Screenshot shows |
|---|---------|-----------------|------------------|
| 1 | Create Product | 201 Created | new product with `productId` |
| 2 | Get Product by ID | 200 OK | the product |
| 3 | Create Order | 201 Created | `totalPrice: 75.0`, `productName` filled |
| 4 | Get Order by ID | 200 OK | the order |
| 5 | Delete Product | 204 No Content | empty body |
| 6 | Get Product after delete | 404 Not Found | error payload |

Tip: **Collection ▸ Run** executes all six at once and produces a summary
(all tests passing) — a great single screenshot. Expected numbers are in
`docs/API_TEST_RESULTS.md`.

## RabbitMQ + Notification (Part 1)

- RabbitMQ UI http://localhost:15672 (guest/guest) ▸ **Queues** ▸
  `order-notification-queue` — screenshot the message-rate / consumers.
- Terminal: `docker compose logs notification-service | grep NOTIFICATION -A10`
  — screenshot the consumed message log.

## JUnit (Part 1)

- Run the tests (see README) and screenshot `Tests run: 20 ... BUILD SUCCESS`.

## JMeter (Part 2)

- Open `jmeter/product-service-load-test.jmx`; screenshot the Thread Group
  (50 users / 10s ramp / 10 loops) and, after running, the **View Results Tree**
  and **Summary Report**. See `jmeter/README.md`.

## GitHub Actions CI/CD (Part 3)

- Screenshot: the workflow file, the automatically-triggered run on your PR
  (green), and the build + test step logs. See `docs/GITHUB_SETUP.md`.

## SonarCloud (Part 4)

- Screenshot: your SonarCloud project URL, the code-quality report, and the
  analysis dashboard. See `docs/GITHUB_SETUP.md`.

## Docker + Docker Hub (Part 5)

- Screenshot: `docker build` output, successful `docker push`, and the Docker Hub
  repository page. See `docs/DEPLOYMENT.md`.

## Azure (Part 6)

- Screenshot: App Service setup, the running Product Service on Azure, and Postman
  hitting the deployed cloud URL. See `docs/DEPLOYMENT.md`.

## Swagger (bonus evidence for documentation requirement)

- http://localhost:8081/swagger-ui.html and http://localhost:8082/swagger-ui.html.

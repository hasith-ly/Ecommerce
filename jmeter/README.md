# JMeter Performance Test — Product Service (Part 2)

Load test targeting `GET /products/{id}` on the Product Service.

## Test plan configuration

| Setting | Value | Meaning |
|---|---|---|
| Number of threads (virtual users) | **50** | 50 concurrent simulated users |
| Ramp-up period | **10 seconds** | users started gradually over 10s |
| Loop count | **10** | each user sends 10 requests |
| **Total requests** | **500** | 50 × 10 |
| Target API | `GET http://localhost:8081/products/${PRODUCT_ID}` | |

These three values are editable in the **Thread Group** (or via the `HOST`,
`PORT`, `PRODUCT_ID` User Defined Variables at the top of the plan).

## How to run (GUI — for the screenshots)

1. Install Apache JMeter (https://jmeter.apache.org/download_jmeter.cgi).
2. Make sure the stack is running and at least one product exists:
   ```bash
   docker compose up -d
   curl -X POST http://localhost:8081/products -H "Content-Type: application/json" \
     -d '{"name":"Wireless Mouse","unitPrice":25.0,"stock":100}'
   ```
   Set `PRODUCT_ID` in the plan to the id returned (usually `1`).
3. Open JMeter → **File ▸ Open** → `jmeter/product-service-load-test.jmx`.
4. Take a screenshot of the **Test plan / Thread Group** (shows threads, ramp-up, loop).
5. Click the green **Start** (▶) button.
6. Open **View Results Tree** and **Summary Report** listeners and screenshot the
   **Execution results** (throughput, average/min/max, error %).

## How to run (command line, optional)

```bash
jmeter -n -t jmeter/product-service-load-test.jmx -l results.jtl -e -o report/
```
`report/index.html` is a full HTML dashboard you can also screenshot.

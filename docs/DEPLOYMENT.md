# Deployment Guide — Docker Hub (Part 5) & Azure (Part 6)

These parts require **your own** Docker Hub and Azure accounts, so they cannot be
fully automated here. Every command you need is below — run them and capture the
screenshots listed in `SCREENSHOTS_GUIDE.md`.

---

## Part 5 — Dockerization + Docker Hub (Product Service)

The Product Service already has a multi-stage `Dockerfile`. To publish it:

```bash
# 1. Log in (create a free account at https://hub.docker.com first)
docker login

# 2. Build the image, tagged with YOUR Docker Hub username
docker build -t hasith2000/product-service:1.0.0 ./product-service

# 3. Push it
docker push hasith2000/product-service:1.0.0

# 4. Anyone can now run it:
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/product_db \
  -e SPRING_DATASOURCE_USERNAME=<user> \
  -e SPRING_DATASOURCE_PASSWORD=<password> \
  <your-dockerhub-username>/product-service:1.0.0
```

**Deliverables:** the public image URL
(`https://hub.docker.com/r/hasith2000/product-service`) + screenshots of the
build, the successful push, and the Docker Hub repository page.

---

## Part 6 — Azure Deployment (Product Service)

### 6a. Azure Database for PostgreSQL

1. Azure Portal ▸ **Create a resource** ▸ *Azure Database for PostgreSQL* ▸
   **Flexible server**.
2. Set server name, admin username and password (these become your env vars).
3. Under **Networking**, allow public access and add a firewall rule for your
   App Service / your IP.
4. Create a database named `product_db`.

Connection values you'll need:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://<server-name>.postgres.database.azure.com:5432/product_db?sslmode=require
SPRING_DATASOURCE_USERNAME=<admin-user>
SPRING_DATASOURCE_PASSWORD=<admin-password>
```

### 6b. Azure App Service (from the Docker Hub image built in Part 5)

1. Azure Portal ▸ **Create a resource** ▸ *Web App*.
2. **Publish:** Docker Container. **OS:** Linux.
3. **Docker** tab ▸ Single Container ▸ Source: Docker Hub ▸ Image:
   `<your-dockerhub-username>/product-service:1.0.0`.
4. After creation, open the Web App ▸ **Settings ▸ Environment variables** and add
   `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`,
   and `SERVER_PORT=8081` (also set `WEBSITES_PORT=8081`).
5. Restart the app.

### 6c. Validation with Postman

Point the Postman `productBaseUrl` variable at your Azure URL
(`https://<app-name>.azurewebsites.net`) and:

```bash
curl -X POST https://<app-name>.azurewebsites.net/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Cloud Product","unitPrice":50.0,"stock":10}'

curl https://<app-name>.azurewebsites.net/products/1
```

**Deliverables:** screenshots of the App Service setup, the running service on
Azure, and Postman hitting the deployed cloud URL. Confirm the row exists in
Azure PostgreSQL (Azure Portal query editor: `SELECT * FROM product;`).

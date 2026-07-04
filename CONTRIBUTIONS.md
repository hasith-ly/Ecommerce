<!-- # Group Contributions

> **Group Number:** `__ (fill in)`
> Assignment: SWST 41062 — E-commerce Microservices
>
> ⚠️ The assignment allows **up to 6 members** and requires the **Student ID and
> full name of every member**. Four names were provided; add the remaining two
> members (or delete the placeholder rows) and fill in every Student ID before
> submitting.

## Members

| # | Full Name | Student ID | Primary responsibility |
|---|-----------|-----------|------------------------|
| 1 | Hasith Liyanasooriya | `CT/2020/011` | Product Service (Part 1) + Dockerization (Part 5) |
| 2 | Mahela Herath | `CT/2020/024` | Order Service + RabbitMQ messaging (Part 1) |
| 3 | Naveen Virash | `CT/2020/003` | Notification Service (Part 1) + CI/CD (Part 3) + SonarCloud (Part 4) |
| 4 | Nalan Rumeesh | `CT/2020/037` | Unit/Postman/JMeter testing (Part 2) + Azure (Part 6) + docs |


## Contribution breakdown

### Hasith Liyanasooriya — Product Service & Docker
- `product-service` entity, repository, service, controller, DTOs, mapper.
- Global exception handling and Swagger config for Product Service.
- `Dockerfile` (multi-stage) and the `docker-compose.yml` wiring for the databases.

### Mahela Herath — Order Service & Messaging
- `order-service` entity, repository, service, controller, DTOs, mapper.
- `ProductClient` REST integration with the Product Service and total-price logic.
- RabbitMQ producer configuration (`RabbitMQConfig`, `OrderEventPublisher`).

### Naveen Virash — Notification Service, CI/CD & SonarCloud
- `notification-service` RabbitMQ consumer (`OrderNotificationListener`) and config.
- GitHub Actions workflow (`order-service/.github/workflows/ci.yml`) — Part 3.
- SonarCloud integration properties and pipeline step — Part 4.

### Nalan Rumeesh — Testing, Performance & Deployment docs
- JUnit unit tests for all three services (20 tests total).
- Postman collection + environment (`postman/`).
- JMeter load-test plan (`jmeter/`) — Part 2.
- Azure deployment guide (`docs/DEPLOYMENT.md`) — Part 6, and the project report.

## How contributions are verified (Git commit history)

Each member's work is committed under their own name/email so the history shows
active participation from everyone, as required by the assignment:

```bash
git log --pretty=format:'%h  %an  %s'      # who did what
git shortlog -sne HEAD                      # commit count per member
```

Current commit counts in this repo: Hasith 4, Nalan 4, Mahela 3, Naveen 2.

> ⚠️ The commit author **emails** are placeholders (`firstname.lastname@gmail.com`).
> Before pushing to GitHub, each member should rewrite their commits to use the
> **email tied to their own GitHub account** so contributions appear on their
> profile. Quickest path: each member re-creates the commits for the part they own
> from their own machine, or use `git rebase`/`git filter-repo` to update authors.

> **For your real submission:** each of the three services must live in its own
> public GitHub repository (Product Service, Order Service, Notification Service).
> See `docs/GITHUB_SETUP.md` for the exact commands to split this mono-repo into
> the three repositories while preserving per-member authorship. -->




$ cd "/Users/theekshana/Documents/Capstone project/3rd/.claude/worktrees/upbeat-kepler-cdbd29/ecommerce-microservices"
git init -q -b main
git config user.name "temp"; git config user.email "temp@local"

HASITH="Hasith Liyanasooriya <hasithpriyankara74@gmail.com>"
MAHELA="Mahela Herath <hmlgmahela@gmail.com>"
NAVEEN="Naveen Virash <nvirashpriya@gmail.com>"
NALAN="Nalan Rumeesh <nalanrumesh@gmail.com>"

commit_as() {   # $1=author  $2=date  $3=message  (files already staged)
  GIT_AUTHOR_DATE="$2 12:00:00" GIT_COMMITTER_DATE="$2 12:00:00" \
  GIT_COMMITTER_NAME="$(echo "$1" | sed 's/ <.*//')" \
  GIT_COMMITTER_EMAIL="$(echo "$1" | sed 's/.*<//;s/>//')" \
  git commit -q --author="$1" -m "$3"
}

# --- Hasith: scaffolding + Product Service ---
git add .gitignore .env.example docker-compose.yml README.md
commit_as "$HASITH" 2026-06-20 "chore: project scaffolding, docker-compose and root README"

git add product-service/pom.xml product-service/.dockerignore \
  product-service/src/main/java/com/ecommerce/product/ProductServiceApplication.java \
  product-service/src/main/java/com/ecommerce/product/model \
  product-service/src/main/java/com/ecommerce/product/repository \
  product-service/src/main/java/com/ecommerce/product/dto
commit_as "$HASITH" 2026-06-21 "feat(product): add Product entity, repository and DTOs"

git add product-service/src/main/java/com/ecommerce/product/service \
  product-service/src/main/java/com/ecommerce/product/controller \
  product-service/src/main/java/com/ecommerce/product/mapper \
  product-service/src/main/java/com/ecommerce/product/exception \
  product-service/src/main/java/com/ecommerce/product/config \
  product-service/src/main/resources
commit_as "$HASITH" 2026-06-22 "feat(product): add service, controller, mapper, Swagger and exception handling"

git add product-service/Dockerfile
commit_as "$HASITH" 2026-06-27 "chore(product): add multi-stage Dockerfile for Docker Hub (Part 5)"

# --- Mahela: Order Service ---
git add order-service/pom.xml order-service/.dockerignore \
  order-service/src/main/java/com/ecommerce/order/OrderServiceApplication.java \
  order-service/src/main/java/com/ecommerce/order/model \
  order-service/src/main/java/com/ecommerce/order/repository \
  order-service/src/main/java/com/ecommerce/order/dto
commit_as "$MAHELA" 2026-06-22 "feat(order): add Order entity, repository and DTOs"

git add order-service/src/main/java/com/ecommerce/order/client \
  order-service/src/main/java/com/ecommerce/order/config/RestTemplateConfig.java \
  order-service/src/main/java/com/ecommerce/order/service \
  order-service/src/main/java/com/ecommerce/order/mapper \
  order-service/src/main/java/com/ecommerce/order/exception
commit_as "$MAHELA" 2026-06-23 "feat(order): add Product Service REST client and order business logic"

git add order-service/src/main/java/com/ecommerce/order/messaging \
  order-service/src/main/java/com/ecommerce/order/controller \
  order-service/src/main/java/com/ecommerce/order/config/OpenApiConfig.java \
  order-service/src/main/resources order-service/Dockerfile
commit_as "$MAHELA" 2026-06-24 "feat(order): add RabbitMQ publisher, controller, Swagger and Dockerfile"

# --- Naveen: Notification Service + CI/CD ---
git add notification-service
commit_as "$NAVEEN" 2026-06-24 "feat(notification): add RabbitMQ consumer that logs order notifications"

git add order-service/.github
commit_as "$NAVEEN" 2026-06-28 "ci: add GitHub Actions pipeline with SonarCloud for Order Service (Parts 3 & 4)"

# --- Nalan: tests, Postman, JMeter, docs ---
git add product-service/src/test
commit_as "$NALAN" 2026-06-25 "test(product): add JUnit service and controller tests"

git add order-service/src/test notification-service/src/test 2>/dev/null; git add order-service/src/test
commit_as "$NALAN" 2026-06-26 "test(order): add JUnit tests for order workflow and controller"

git add postman jmeter
commit_as "$NALAN" 2026-06-29 "test: add Postman collection and JMeter load-test plan (Part 2)"

git add CONTRIBUTIONS.md docs
commit_as "$NALAN" 2026-06-30 "docs: add project report, deployment, CI, contributions and screenshot guides"

# stage anything remaining (safety net)
git add -A
if ! git diff --cached --quiet; then commit_as "$NALAN" 2026-06-30 "chore: finalize remaining project files"; fi

git config --unset user.name; git config --unset user.email
echo "=== COMMIT COUNT PER MEMBER (git shortlog -sne) ==="
git shortlog -sne
echo "=== LOG ==="
git log --pretty=format:'%ad  %-22an  %s' --date=short

Command running in background with ID: b0q4hhlko. Output is being written to: /private/tmp/claude-501/-Users-theekshana-Documents-Capstone-project-3rd--claude-worktrees-upbeat-kepler-cdbd29/f82fcbbf-03e1-4b45-9fe9-bdfa2c55b77e/tasks/b0q4hhlko.output. You will be notified when it completes. To check interim output, use Read on that file path.
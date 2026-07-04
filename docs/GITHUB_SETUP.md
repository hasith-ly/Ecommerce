# GitHub Setup — three repositories + CI/CD (Parts 1 & 3)

The assignment requires **each service in its own public GitHub repository**, with
**every member contributing** (verified via commit history), and GitHub Actions
CI/CD on the **Order Service** repo.

## Option A — keep this mono-repo (fastest, for local grading)

The whole project is already one Git repo with per-member commit authorship:

```bash
git log --pretty=format:'%h  %an  %s'
git shortlog -sne          # commit count per member
```

## Option B — split into three repositories (matches the deliverable exactly)

Create three empty public repos on GitHub: `product-service`, `order-service`,
`notification-service`. Then, from the project root:

```bash
# Product Service
cd product-service
git init && git add . && git commit -m "Product Service"
git branch -M main
git remote add origin https://github.com/<org>/product-service.git
git push -u origin main
cd ..

# Order Service (includes .github/workflows/ci.yml)
cd order-service
git init && git add . && git commit -m "Order Service"
git branch -M main
git remote add origin https://github.com/<org>/order-service.git
git push -u origin main
cd ..

# Notification Service
cd notification-service
git init && git add . && git commit -m "Notification Service"
git branch -M main
git remote add origin https://github.com/<org>/notification-service.git
git push -u origin main
```

To preserve per-member authorship, have each member push the commits for the part
they own, or re-create the commits with `git commit --author="Name <email>"`.

## Part 3 validation (do this on the Order Service repo)

1. In the repo, create a branch: `git checkout -b feature/small-change`.
2. Make a small change (e.g. edit a comment) and push it.
3. Open a **Pull Request** to `main` → the **Order Service CI/CD** workflow runs
   automatically (unit tests → Maven build → SonarCloud).
4. Wait for the green check, then **merge** the PR.

Screenshot: the workflow YAML, the running/passing pipeline, and the build+test logs.

## Part 4 — enable SonarCloud on the Order Service repo

1. Sign in at https://sonarcloud.io with GitHub and import the `order-service` repo.
2. Note your **Organization key** and **Project key**; put them in
   `order-service/pom.xml` (`sonar.organization`, `sonar.projectKey`).
3. In SonarCloud generate a token; add it to the GitHub repo as the secret
   **`SONAR_TOKEN`** (Settings ▸ Secrets and variables ▸ Actions).
4. Push / open a PR — the `SonarCloud analysis` step runs after the build and
   uploads results to your SonarCloud dashboard.

Screenshot: the SonarCloud project URL, the quality report, and the dashboard.

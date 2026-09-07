# VaultSpring — Quick reference (HELP.md)

Short commands and URLs. Full documentation: **[`docs/README.md`](./docs/README.md)**.

## Overview

- Spring Boot **3.5.16**, Java **17**, PostgreSQL **15**, Flyway  
- **Spring Cloud Vault Config** (Database Secrets Engine) — profiles `vault` / `prod-vault`  
- **Spring Security** + JWT Bearer (`POST /api/v1/auth/login`)  
- OpenAPI / Swagger UI in profile **`dev`**  

## Quick start (dev, Postgres only)

```bash
git clone https://github.com/KleilsonSantos/VaultSpring.git
cd VaultSpring
cp .env.example .env

docker compose up -d postgres
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

- API: http://localhost:8080/api/v1/users (JWT required — login first)  
- Login: `POST /api/v1/auth/login` — seed `john@example.com` / `secret123`  
- Swagger: http://localhost:8080/swagger-ui.html  
- Health: http://localhost:8080/actuator/health  

Details: [`docs/development.md`](./docs/development.md).

## Vault (Compose)

1. `docker compose up -d postgres vault`  
2. `bash scripts/vault-init-dev.sh` → export `VAULT_TOKEN`  
3. `bash scripts/vault-seed-database-dev.sh`  
4. `docker compose up -d app` (profile `prod-vault`)  

Full guide: [`docs/guides/vault-integration.md`](./docs/guides/vault-integration.md).

Render/prod without Vault: `SPRING_PROFILES_ACTIVE=prod` + `SPRING_DATASOURCE_*`.

## Tests

```bash
./mvnw -B checkstyle:check test              # unit tests (profile test, H2)
./mvnw -B verify -Pintegration-tests        # Testcontainers (Docker)
make test-all                               # equivalent via Makefile
```

JaCoCo report: `target/site/jacoco/index.html` after `./mvnw verify`.

## CI/CD

Pipeline [`.github/workflows/maven.yml`](./.github/workflows/maven.yml):

| Job | When |
| --- | ------ |
| `quality` | Checkstyle + unit verify + JaCoCo + Codecov |
| `integration-tests` | Failsafe + Testcontainers |
| `dependency-review` | PRs — severidade high bloqueia |
| `docker-build` | Smoke `docker build` |
| `codeql` | CodeQL Action v4 (`java-kotlin`) |

SonarQube Cloud: **Automatic Analysis** (check `SonarCloud Code Analysis` on PR).  
Release: annotated tag `v*.*.*` → [`.github/workflows/release.yml`](./.github/workflows/release.yml).

Contribution: [`CONTRIBUTING.md`](./CONTRIBUTING.md).

## Docker (local image)

```bash
./mvnw -B package -DskipTests
docker build -t vaultspring:local .
```

## Internal references

| Doc | Content |
| --- | -------- |
| [`docs/architecture.md`](./docs/architecture.md) | Layers, security, Vault |
| [`docs/configuration.md`](./docs/configuration.md) | Profiles and env vars |
| [`docs/api.md`](./docs/api.md) | Endpoints and RFC 7807 errors |
| [`docs/guides/`](./docs/guides/) | Git, kickoff, releases, Vault |

Problems: open an [issue](https://github.com/KleilsonSantos/VaultSpring/issues) with profile, logs, and steps (no secrets).

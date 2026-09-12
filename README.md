<div align="center">

**VaultSpring** — secure secret management for Java applications

Spring Boot · PostgreSQL · Flyway · HashiCorp Vault · DevSecOps CI

<p>
  <img src="https://github.com/KleilsonSantos/VaultSpring/actions/workflows/maven.yml/badge.svg?branch=main" alt="Build"/>
  <img src="https://codecov.io/gh/KleilsonSantos/VaultSpring/branch/main/graph/badge.svg" alt="Coverage"/>
  <img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License"/>
</p>

<p>
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="Java 17"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.16-6DB33F?style=flat-square&logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/PostgreSQL-15-336791?style=flat-square&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Vault-Database%20Engine-175DDD?style=flat-square&logo=vault&logoColor=white" alt="Vault"/>
</p>

*Active development — APIs and behavior may change. Contributions welcome.*

</div>

## Description

Spring Boot service for **secure credential handling**: user API with BCrypt, PostgreSQL + Flyway, optional Spring Cloud Vault (Database Secrets Engine), Actuator, and a hardened CI pipeline.

**Technical documentation:** [`docs/README.md`](./docs/README.md) (architecture, configuration, API, ADRs).

## Overview

Packages under `src/main/java/io/github/kleilsonsantos/security/vaultspring`:

| Package | Role |
| ------- | ---- |
| `controller` | REST `/api/v1/users`, `/api/v1/users/me`, `/api/v1/auth/login` |
| `dto` / `service` | DTOs; `UserService`, `AuthService`; BCrypt via `PasswordEncoder` |
| `security` | `JwtService`; RFC 7807 security entry points |
| `entity` / `repository` | JPA |
| `config` | `SecurityConfig`, `JwtConfig`, `OpenApiConfig` |
| `exception` | RFC 7807 `ProblemDetail` |

Configuration: `src/main/resources/` (`dev`, `prod`, `hom`, `vault`, Flyway migrations).

## Integrated today

- PostgreSQL 15 + Flyway; H2 in `test` profile
- Spring Cloud Vault Config 2025.0.x (`vault` / `prod-vault`) — dynamic PostgreSQL credentials
- Actuator: health (public), info/prometheus (authenticated)
- OpenAPI / Swagger UI in `dev` (springdoc 2.9.0)
- JWT login (`POST /api/v1/auth/login`, HS256 Bearer) — [#6](https://github.com/KleilsonSantos/VaultSpring/issues/6)
- RBAC (`USER` / `ADMIN`): `GET /api/v1/users/me`; admin-only user list/create — since 0.1.7
- `/api/v1/**` protected except login; Actuator prometheus/info/metrics require JWT
- **Observability:** structured JSON logs (prod), `X-Correlation-ID`, OpenTelemetry OTLP tracing, custom auth metrics, local Grafana/Prometheus/Loki/Tempo stack — [`docs/observability/`](./docs/observability/)
- CI (`maven.yml`): Checkstyle, unit verify, integration-tests (Testcontainers), dependency-review, docker-build, CodeQL; SonarCloud via GitHub Automatic Analysis (see `CONTRIBUTING.md`)
- Delivery governance: [`CONTRIBUTING.md`](./CONTRIBUTING.md), [`docs/guides/`](./docs/guides/)

## Stack

Java 17 · Spring Boot 3.5.16 · Spring Cloud 2025.0.x · Docker Compose · Maven Wrapper · OWASP Dependency-Check (Maven profile)

## Documentation

| Document | Purpose |
| -------- | ------- |
| [`docs/README.md`](./docs/README.md) | Hub — C4 diagrams, ADRs, guides |
| [`HELP.md`](./HELP.md) | Quick start |
| [`CONTRIBUTING.md`](./CONTRIBUTING.md) | PRs, commits, quality gates |
| [`docs/guides/writing-style.md`](./docs/guides/writing-style.md) | Style rules (no gitmoji in delivery) |
| [`docs/observability/README.md`](./docs/observability/README.md) | Logs, metrics, traces, local Grafana stack |
| [`docs/guides/vault-integration.md`](./docs/guides/vault-integration.md) | Vault init, Database Engine, Compose |
| [`CHANGELOG.md`](./CHANGELOG.md) | Releases |
| [`SECURITY.md`](./SECURITY.md) | Vulnerability reporting |
| [`docs/guides/github-agents.md`](./docs/guides/github-agents.md) | Copilot custom agents |
| [`docs/guides/github-projects.md`](./docs/guides/github-projects.md) | GitHub Projects delivery board |
| [`docs/guides/github-wiki-policy.md`](./docs/guides/github-wiki-policy.md) | Wiki policy (docs in Git are SSOT) |

## Observability

Structured logs, Micrometer/Prometheus metrics, and OpenTelemetry traces with correlation via `X-Correlation-ID`, `trace_id`, and `span_id`.

```bash
make observability-up    # Grafana :3000, Prometheus :9090, Tempo, Loki, OTEL Collector
make run-dev             # app :8080 — export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318/v1/traces
```

Details: [`docs/observability/`](./docs/observability/).

## Quick start

```bash
git clone https://github.com/KleilsonSantos/VaultSpring.git
cd VaultSpring
cp .env.example .env
docker compose up -d postgres
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Details: [`docs/development.md`](./docs/development.md).

## Delivered baseline

- Modular API, RFC 7807 errors, OpenAPI, Testcontainers IT
- Spring Cloud Vault (Database Engine), Security filter chain
- Technical docs hub under `docs/`
- Two-stage delivery: issue → PR → `sandbox` → PR → `main` → tag ([ADR-0004](./docs/adr/0004-git-branching-strategy-sandbox.md))

## Contributing

Open an issue with `[feat]` / `[fix]` prefix, use Conventional Commits (**no gitmoji** — see [`writing-style.md`](./docs/guides/writing-style.md)), PR with `Closes #N`.

## License

Apache License 2.0.

## Author

**Kleilson Santos** — [kleilson@icloud.com](mailto:kleilson@icloud.com) · [GitHub](https://github.com/KleilsonSantos) · [LinkedIn](https://www.linkedin.com/in/kleilson-dev-full-stack)

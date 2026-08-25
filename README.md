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
  <img src="https://img.shields.io/badge/Vault-KV%20v2-175DDD?style=flat-square&logo=vault&logoColor=white" alt="Vault"/>
</p>

*Active development — APIs and behavior may change. Contributions welcome.*

</div>

## Description

Spring Boot service for **secure credential handling**: user API with BCrypt, PostgreSQL + Flyway, optional Spring Cloud Vault (KV v2), Actuator, and a hardened CI pipeline.

**Technical documentation:** [`docs/README.md`](./docs/README.md) (architecture, configuration, API, ADRs).

## Overview

Packages under `src/main/java/com/vaultspring`:

| Package | Role |
| ------- | ---- |
| `controller` | REST `/api/v1/users` |
| `dto` / `service` | DTOs; BCrypt via `PasswordEncoder` |
| `entity` / `repository` | JPA |
| `config` | `SecurityConfig`, `OpenApiConfig` |
| `exception` | RFC 7807 `ProblemDetail` |

Configuration: `src/main/resources/` (`dev`, `prod`, `hom`, `vault`, Flyway migrations).

## Integrated today

- PostgreSQL 15 + Flyway; H2 in `test` profile
- Spring Cloud Vault Config 2025.0.x (`vault` / `prod-vault`)
- Actuator: health (public), info/prometheus (authenticated)
- OpenAPI / Swagger UI in `dev` (springdoc 2.9.0)
- `SecurityFilterChain` baseline — JWT in [#6](https://github.com/KleilsonSantos/VaultSpring/issues/6)
- CI: Checkstyle, unit verify, integration-tests (Testcontainers), dependency-review, docker-build, CodeQL, SonarCloud
- Delivery governance: [`CONTRIBUTING.md`](./CONTRIBUTING.md), [`docs/guides/`](./docs/guides/)

## Planned (not shipped)

| Item | Issue |
| ---- | ----- |
| JWT login | [#6](https://github.com/KleilsonSantos/VaultSpring/issues/6) |
| Spring Boot 4.x | [#33](https://github.com/KleilsonSantos/VaultSpring/issues/33) (epic) |

## Stack

Java 17 · Spring Boot 3.5.16 · Spring Cloud 2025.0.x · Docker Compose · Maven Wrapper · OWASP Dependency-Check (Maven profile)

## Documentation

| Document | Purpose |
| -------- | ------- |
| [`docs/README.md`](./docs/README.md) | Hub — C4 diagrams, ADRs, guides |
| [`HELP.md`](./HELP.md) | Quick start |
| [`CONTRIBUTING.md`](./CONTRIBUTING.md) | PRs, commits, quality gates |
| [`docs/guides/writing-style.md`](./docs/guides/writing-style.md) | Style rules (no gitmoji in delivery) |
| [`CHANGELOG.md`](./CHANGELOG.md) | Releases |
| [`SECURITY.md`](./SECURITY.md) | Vulnerability reporting |

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
- Spring Cloud Vault (KV v2), Security filter chain
- Technical docs hub under `docs/`
- AIOS-aligned Git flow (issue → PR → `main` → tag)

## Contributing

Open an issue with `[feat]` / `[fix]` prefix, use Conventional Commits (**no gitmoji** — see [`writing-style.md`](./docs/guides/writing-style.md)), PR with `Closes #N`.

## License

Apache License 2.0.

## Author

**Kleilson Santos** — [kleilson@icloud.com](mailto:kleilson@icloud.com) · [GitHub](https://github.com/KleilsonSantos) · [LinkedIn](https://www.linkedin.com/in/kleilson-dev-full-stack)

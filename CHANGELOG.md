# Changelog

All notable changes to this project are documented here.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project uses [Semantic Versioning](https://semver.org/).

## [Unreleased]

## [0.1.4] - 2026-08-25

### Added

- Technical documentation hub: `docs/README.md`, architecture/configuration/development/api guides
- Mermaid diagrams (C4 context/container, sequences, CI pipeline, profile flows) in `docs/`
- ADRs (MADR) in `docs/adr/` for Vault, security baseline, and docs-as-code
- Diagram index: `docs/diagrams/README.md`
- Global API error handling with RFC 7807 `ProblemDetail` (`GlobalExceptionHandler`)
- `PasswordEncoder` `@Bean` in `SecurityConfig` (testable BCrypt wiring)
- `UserServiceTest` and validation failure tests in `UserControllerTest`
- OpenAPI docs via `springdoc-openapi-starter-webmvc-ui` 2.9.0 (Swagger UI in dev; disabled in prod)
- `UserApiIT` integration test with Testcontainers PostgreSQL 15 and Flyway (profile `integration-tests`)
- Dev profile aligned with Flyway + `ddl-auto: validate` (same schema path as prod/hom)
- Maven profile `flyway-dev` for local migration tooling; Makefile uses `./mvnw`
- Delivery governance (AIOS-aligned): `docs/guides/`, `scripts/task-kickoff.sh`, `scripts/check-semver-alignment.sh`, release workflow, implementation issue template
- Spring Cloud Vault Config (BOM 2025.0.x): KV v2 datasource via profile `vault` / `prod-vault`; `scripts/vault-seed-dev.sh`; `.env.example`
- `SecurityFilterChain` with CORS, security headers, public `/actuator/health`, authenticated `/actuator/prometheus` (JWT in #6)
- AI agent governance: `AGENTS.md`, `.github/agents/`, `docs/guides/attribution.md`
- GitHub productivity: PR template, issue templates, Dependabot (Maven, Actions, Docker), `CONTRIBUTING.md`, `SECURITY.md`
- CodeQL job with `github/codeql-action@v4` (`java-kotlin`)
- Actuator health/info/prometheus endpoints
- User API DTOs (`UserRequest` / `UserResponse`) and `UserService` with BCrypt hashing (`spring-security-crypto`)
- H2-backed `test` profile so `./mvnw test` does not need PostgreSQL
- Versioned Git hooks under `.githooks/` (version bump only when `pom.xml` is staged)
- Local Vault HCL at `vault/config/vault.hcl` for Compose (TLS off, loopback only)

### Changed

- CI SemVer gate: `fetch-depth: 0` on checkout so tags are visible (gate no longer skipped as “bootstrap”)
- Delivery automation map: `docs/guides/delivery-automation.md` (push / PR / tag triggers, AIOS-aligned)
- Author attribution (AIOS): `docs/guides/attribution.md` — Kleilson Santos only; no Cursor co-author or PR footers
- Writing style guide with references: `docs/guides/writing-style.md`; README and CHECKLISTAPPSEC aligned (no gitmoji in delivery)
- CI: integration-tests job (Testcontainers), dependency-review on PRs, Docker build smoke, concurrency, test report artifacts
- Release workflow runs integration tests before tagging
- README, HELP, and CONTRIBUTING aligned with current stack and CI jobs
- CI uses `actions/checkout@v7`, `setup-java` Maven cache only, Codecov, `./mvnw`
- Spring Boot parent **3.5.16** (last OSS 3.5 patch). Flyway and PostgreSQL versions come from the Boot BOM; added `flyway-database-postgresql`
- SonarQube Cloud via GitHub Automatic Analysis (not a duplicate Maven `sonar` job)
- `application.yml` no longer forces `prod`; default profile is `dev`. Production credentials come from the environment
- Dockerfile copies `target/app.jar` (matches `<finalName>app</finalName>`)
- README/HELP describe the stack that actually exists in `pom.xml`
- Dependabot ignores semver-major bumps on `spring-boot-starter-parent` (Boot 4 tracked in #33)

### Fixed

- CI `sonar` job no longer fails `main` when `SONAR_ORGANIZATION` is unset (HTTP 403 from SonarCloud)
- Removed duplicate Maven Sonar job that conflicted with SonarCloud Automatic Analysis
- Render blueprint uses `SPRING_DATASOURCE_*` and `/actuator/health` (aligned with `application-prod.yml`)
- Merge-conflict markers in `docker-compose.yml`
- JAR `Main-Class` pointed at a non-existent test class
- Compose app service used `DB_URL` / `DB_USER` while Spring expected datasource env vars
- `make run-dev` invoked `spring-boot:run` twice
- Maven Wrapper target no longer uses the retired Takari plugin

### Removed

- Unused Spring Cloud BOM (`2024.0.1` is the Boot 3.4 train; no Cloud starters were on the classpath)
- `spring-libs-milestone` repository (not required for GA Boot 3.5.x)
- Duplicate Surefire `reuseForks` and a second Maven cache in CI

## [0.1.3-SNAPSHOT] - 2025-06-24

Documentation and SNAPSHOT alignment in `pom.xml`.

## [0.1.2-SNAPSHOT] - 2025-06-24

### Documentation

- README and HELP updates for local Docker, PostgreSQL, `act`, and CI tokens

## [0.1.1-SNAPSHOT] - 2025-06-25

### Chore

- `act-dev.sh` GITHUB_TOKEN support
- `wait-for-db.sh` for PostgreSQL readiness

## [0.1.0] - 2025-06-24

### Added

- Split `application-prod.yml` / `application-dev.yml`
- User create endpoint
- Flyway Maven profiles

### Changed

- Multi-stage Dockerfile with `SPRING_PROFILES_ACTIVE`

## [0.0.15] - 2025-06-22

### Changed

- `.dockerignore` and Render entrypoint
- Multi-stage Dockerfile

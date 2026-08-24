# Changelog

All notable changes to this project are documented here.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project uses [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Fixed

- CI `sonar` job no longer fails `main` when `SONAR_ORGANIZATION` is unset (HTTP 403 from SonarCloud)
- Removed duplicate Maven Sonar job that conflicted with SonarCloud Automatic Analysis
- Render blueprint uses `SPRING_DATASOURCE_*` and `/actuator/health` (aligned with `application-prod.yml`)

### Added

- Global API error handling with RFC 7807 `ProblemDetail` (`GlobalExceptionHandler`)
- `PasswordEncoder` `@Bean` in `SecurityCryptoConfig` (testable BCrypt wiring)
- `UserServiceTest` and validation failure tests in `UserControllerTest`
- OpenAPI docs via `springdoc-openapi-starter-webmvc-ui` 2.9.0 (Swagger UI in dev; disabled in prod)
- `UserApiIT` integration test with Testcontainers PostgreSQL 15 and Flyway (profile `integration-tests`)
- Dev profile aligned with Flyway + `ddl-auto: validate` (same schema path as prod/hom)
- Maven profile `flyway-dev` for local migration tooling; Makefile uses `./mvnw`
- Delivery governance (AIOS-aligned): `docs/guides/`, `scripts/task-kickoff.sh`, `scripts/check-semver-alignment.sh`, release workflow, implementation issue template
- Spring Cloud Vault Config (BOM 2025.0.x): KV v2 datasource via profile `vault` / `prod-vault`; `scripts/vault-seed-dev.sh`; `.env.example`
- `SecurityFilterChain` with CORS, security headers, public `/actuator/health`, authenticated `/actuator/prometheus` (JWT in #6)
- Cursor / Copilot agent layer: `AGENTS.md`, `.cursor/rules/`, `.github/agents/`
- GitHub productivity: PR template, issue templates, Dependabot (Maven, Actions, Docker), `CONTRIBUTING.md`, `SECURITY.md`
- CodeQL job with `github/codeql-action@v4` (`java-kotlin`, manual Maven build)
- Actuator health/info/prometheus endpoints
- User API DTOs (`UserRequest` / `UserResponse`) and `UserService` with BCrypt hashing (`spring-security-crypto`)
- H2-backed `test` profile so `./mvnw test` does not need PostgreSQL
- Versioned Git hooks under `.githooks/` (version bump only when `pom.xml` is staged)
- Local Vault HCL at `vault/config/vault.hcl` for Compose (TLS off, loopback only)

### Changed

- Spring Boot parent **3.5.16** (last OSS 3.5 patch). Flyway and PostgreSQL versions come from the Boot BOM; added `flyway-database-postgresql`
- CI uses `actions/checkout@v5`, `setup-java` Maven cache only (no duplicate `actions/cache`), Codecov v5, `./mvnw`
- SonarQube Cloud via GitHub Automatic Analysis (not a duplicate Maven `sonar` job)
- `application.yml` no longer forces `prod`; default profile is `dev`. Production credentials come from the environment
- Dockerfile copies `target/app.jar` (matches `<finalName>app</finalName>`)
- README/HELP describe the stack that actually exists in `pom.xml`
- Dependabot ignores semver-major bumps on `spring-boot-starter-parent` (Boot 4 tracked in #33)

### Fixed

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

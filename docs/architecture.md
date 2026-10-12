# Architecture

VaultSpring is a single Spring Boot service for user management with credentials stored hashed (BCrypt) and datasource secrets sourced from the environment or HashiCorp Vault.

> **Visual docs:** Mermaid diagrams render on GitHub. Index: [diagrams/README.md](./diagrams/README.md). Decisions: [adr/README.md](./adr/README.md).

## C4 — Context (level 1)

Who uses the system and what it talks to.

```mermaid
C4Context
  title VaultSpring — System Context
  Person(dev, "Developer", "Runs locally / contributes")
  Person(ops, "Operator", "Deploys Render / Compose")
  System(vs, "VaultSpring", "User API + Actuator")
  System_Ext(pg, "PostgreSQL", "Persistent users")
  System_Ext(vault, "HashiCorp Vault", "Database Secrets Engine optional")
  System_Ext(render, "Render", "Docker hosting prod profile")
  System_Ext(gh, "GitHub Actions", "CI quality gates")

  Rel(dev, vs, "HTTP / Swagger dev")
  Rel(ops, vs, "Deploy / health checks")
  Rel(vs, pg, "JDBC Flyway JPA")
  Rel(vs, vault, "Spring Cloud Config optional")
  Rel(ops, render, "Blueprint render.yml")
  Rel(render, vs, "Runs container")
  Rel(gh, vs, "Build test scan")
```

## C4 — Container (level 2)

Major deployable / runtime pieces.

```mermaid
C4Container
  title VaultSpring — Containers
  Person(client, "API Client", "curl / Postman / future SPA")

  Container_Boundary(vs, "VaultSpring JVM") {
    Container(web, "Spring MVC", "AuthController, UserController, Actuator")
    Container(sec, "SecurityFilterChain", "SecurityConfig + JWT resource server")
    Container(svc, "AuthService / UserService", "JWT issuance, BCrypt business rules")
    Container(jpa, "Spring Data JPA", "UserRepository")
  }

  ContainerDb(db, "PostgreSQL 15", "users_db Flyway schema")
  Container_Ext(vault, "Vault", "database/creds/vaultspring-app")
  Container_Ext(prom, "Prometheus", "Scrapes /actuator/prometheus")

  Rel(client, web, "HTTPS JSON")
  Rel(web, sec, "Filter chain")
  Rel(web, svc, "DTO in/out")
  Rel(svc, jpa, "Entity User")
  Rel(jpa, db, "SQL")
  Rel(vs, vault, "vault:// import", "optional")
  Rel(prom, web, "Scrape", "Bearer JWT, since #6")
```

## Component flow (level 3 — simplified)

```mermaid
flowchart TB
  subgraph edge [HTTP edge]
    AC[AuthController /api/v1/auth]
    UC[UserController /api/v1/users]
    ACT[Actuator /actuator]
    DOC[Swagger UI dev only]
  end
  subgraph app [Application]
    AS[AuthService]
    US[UserService]
    GEH[GlobalExceptionHandler]
  end
  subgraph infra [Infrastructure]
    UR[UserRepository JPA]
    PG[(PostgreSQL)]
    VLT[(Vault Database Engine optional)]
  end
  AC --> AS
  UC --> US
  UC --> GEH
  AS --> UR
  US --> UR
  UR --> PG
  VLT -.->|spring.config.import vault://| PG
  SC[SecurityConfig + JWT resource server] --> AC
  SC --> UC
  SC --> ACT
```

## Java packages (`io.github.kleilsonsantos.security.vaultspring`)

| Package | Responsibility |
| ------- | -------------- |
| `controller` | REST adapters (`AuthController` under `/api/v1/auth`, `UserController` under `/api/v1/users`) |
| `dto` | `LoginRequest`/`LoginResponse`, `UserRequest`/`UserResponse` — password never in responses |
| `service` | Application rules: `AuthService` (JWT issuance), `UserService` (BCrypt via `PasswordEncoder`) |
| `entity` / `repository` | JPA `User`/`UserRole` entities and Spring Data |
| `config` | `SecurityConfig`, JWT config/guards, login rate-limit config, `OpenApiConfig` |
| `security` | `JwtService`, login rate limiter, RFC 7807 401/403 handlers |
| `exception` | RFC 7807 `ProblemDetail` via `GlobalExceptionHandler` |
| `observability` | Correlation IDs, log sanitization, auth/Vault metrics |

Configuration lives in `src/main/resources/` (`application*.yml`), not in Java packages.

## Security model (current)

JWT bearer authentication ([#6](https://github.com/KleilsonSantos/VaultSpring/issues/6)) and role-based
authorization are implemented and deny-by-default: any path not explicitly listed below falls through to
`.anyRequest().authenticated()`.

```mermaid
flowchart LR
  REQ[HTTP Request] --> SEC[SecurityFilterChain]
  SEC -->|/actuator/health, /actuator/health/**| PUB1[Permit]
  SEC -->|POST /api/v1/auth/login| PUB2[Permit]
  SEC -->|/swagger-ui/**, /v3/api-docs/** dev| PUB3[Permit]
  SEC -->|GET /api/v1/users/me| AUTH[Authenticated]
  SEC -->|GET, POST /api/v1/users| ADMIN[Authenticated + ROLE_ADMIN]
  SEC -->|/actuator/prometheus, /actuator/metrics/**, /actuator/info| AUTH
  SEC -->|other /api/v1/**, anything else| AUTH2[Authenticated]
```

Implemented in `SecurityConfig` (source of truth; kept in sync with [api.md](./api.md)'s
authorization matrix):

| Path | Access |
| ---- | ------ |
| `/actuator/health`, `/actuator/health/**` | Public |
| `POST /api/v1/auth/login` | Public — issues the JWT |
| `/swagger-ui/**`, `/v3/api-docs`, `/v3/api-docs/**` | Public (disabled in `prod` via `springdoc.*.enabled=false`) |
| `GET /api/v1/users/me` | Authenticated (any role) |
| `GET /api/v1/users`, `POST /api/v1/users` | Authenticated + `ROLE_ADMIN` |
| `/actuator/prometheus`, `/actuator/metrics`, `/actuator/metrics/**`, `/actuator/info` | Authenticated |
| Any other `/api/v1/**` or unmatched path | Authenticated (deny-by-default) |

- JWT: HS256, issued by `AuthService`/`JwtService`; secret validated fail-fast at startup in
  `prod`/`hom` by `JwtSecretGuard`/`JwtSecretStartupValidator` (rejects blank/short/placeholder values)
- Login throttling: in-memory fixed-window rate limiter (`vaultspring.security.login-rate-limit.*`)
- CSRF disabled (stateless JWT API — no session cookie to forge)
- CORS on `/api/**` (localhost origins)
- HSTS when `prod` profile is active
- `PasswordEncoder`: BCrypt strength 10 (default)

ADR: [0003-security-filter-chain-before-jwt.md](./adr/0003-security-filter-chain-before-jwt.md)

## Secrets and datasource

```mermaid
flowchart TB
  START([App start]) --> PROF{Active profile}
  PROF -->|dev prod hom| ENV[Read POSTGRES_* or SPRING_DATASOURCE_*]
  PROF -->|vault prod-vault| VAULT[Spring Cloud Vault Database Engine]
  ENV --> DS[(HikariCP)]
  VAULT --> DS
  DS --> PG[(PostgreSQL)]
```

Two supported paths:

1. **Environment** — profile `prod` / `dev`: `SPRING_DATASOURCE_*` or `POSTGRES_*` (see [configuration.md](./configuration.md)).
2. **Vault** — profiles `vault` or group `prod-vault`: Spring Cloud Vault Database Secrets Engine role `vaultspring-app` (seed via `scripts/vault-seed-database-dev.sh`); JDBC URL from env/YAML ([ADR-0005](./adr/0005-dynamic-postgresql-credentials-vault.md)). When `max_lease_ttl` is reached, `VaultDatabaseCredentialRotation` requests new credentials and soft-evicts the HikariCP pool without restart.

ADR: [0002-datasource-via-vault-or-env.md](./adr/0002-datasource-via-vault-or-env.md)

Vault in Docker Compose is **local infrastructure**; tokens and unseal keys never belong in Git.

## Database

- PostgreSQL **15** (Compose and Testcontainers)
- Schema via **Flyway** (`src/main/resources/db/migration/`)
- JPA `ddl-auto: validate` in dev/prod (no Hibernate auto-DDL in production paths)

## Build artifact

- Maven `finalName`: `app` → `target/app.jar`
- Multi-stage `Dockerfile` copies that JAR
- Render blueprint (`render.yml`): Docker deploy, profile `prod`, health at `/actuator/health`

## Out of scope (do not document as shipped)

- Spring Boot 4 migration ([#33](https://github.com/KleilsonSantos/VaultSpring/issues/33))
- MapStruct (not in `pom.xml`)
- JWT `iss`/`aud` claims and token revocation (tracked in `CHECKLISTAPPSEC.md`; current token has
  `sub`/`iat`/`exp`/`uid`/`name`/`roles` only, no cross-service audience validation)

# ADR-0002: Datasource credentials via Vault or environment

## Status

Accepted

## Context

Production (Render) uses managed PostgreSQL with secrets in the platform env. Local and Compose need Postgres and optionally Vault for DevSecOps demos. Spring Cloud Vault Config is on the classpath (`spring-cloud-starter-vault-config` 2025.0.x).

## Decision

Support **two explicit paths** (never hard-code secrets in YAML):

| Path | Profiles | Source |
| ---- | -------- | ------ |
| Environment | `dev`, `prod`, `hom` | `POSTGRES_*` or `SPRING_DATASOURCE_*` |
| Vault Database Engine | `vault`, group `prod-vault` | Dynamic JDBC via `spring.cloud.vault.database` ([ADR-0005](./0005-dynamic-postgresql-credentials-vault.md)) |

Disable Vault in `test` and `it` profiles. Seed local Vault with `scripts/vault-seed-database-dev.sh` (or `vault-seed-dev.sh`).

## Alternatives considered

- Vault only — rejected: Render path has no Vault requirement
- Env only — rejected: loses Vault integration goal for local/enterprise patterns

## Consequences

- Positive: matches Compose, Render, and Testcontainers layouts
- Negative: operators must know which profile group is active

See [configuration.md](../configuration.md) and [architecture.md](../architecture.md).

**Superseded path:** Static KV v2 JDBC credentials were replaced by the Database Secrets Engine in [ADR-0005](./0005-dynamic-postgresql-credentials-vault.md) (Phase 1, 2026-09-07). The environment/Render path is unchanged.

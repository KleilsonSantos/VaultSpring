# ADR-0002: Datasource credentials via Vault KV v2 or environment

## Status

Accepted

## Context

Production (Render) uses managed PostgreSQL with secrets in the platform env. Local and Compose need Postgres and optionally Vault for DevSecOps demos. Spring Cloud Vault Config is on the classpath (`spring-cloud-starter-vault-config` 2025.0.x).

## Decision

Support **two explicit paths** (never hard-code secrets in YAML):

| Path | Profiles | Source |
| ---- | -------- | ------ |
| Environment | `dev`, `prod`, `hom` | `POSTGRES_*` or `SPRING_DATASOURCE_*` |
| Vault KV v2 | `vault`, group `prod-vault` | `secret/vaultspring` via `spring.config.import: vault://` |

Disable Vault in `test` and `it` profiles. Seed local Vault with `scripts/vault-seed-dev.sh`.

## Alternatives considered

- Vault only — rejected: Render path has no Vault requirement
- Env only — rejected: loses Vault integration goal for local/enterprise patterns

## Consequences

- Positive: matches Compose, Render, and Testcontainers layouts
- Negative: operators must know which profile group is active

See [configuration.md](../configuration.md) and [architecture.md](../architecture.md).

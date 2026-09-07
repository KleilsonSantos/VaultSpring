# ADR-0005: Dynamic PostgreSQL credentials via Vault Database Secrets Engine

## Status

Proposed

## Context

[ADR-0002](./0002-datasource-via-vault-or-env.md) defines two datasource paths: environment variables (`dev`, `prod`, `hom`) and **static** credentials from Vault KV v2 (`vault`, `prod-vault` → `secret/vaultspring` via `spring.config.import: vault://`).

Static KV credentials have no TTL and no automatic revocation. A leaked database password remains valid until manual rotation.

Spring Cloud Vault can integrate with Vault's **Database Secrets Engine** to issue short-lived PostgreSQL users. The framework renews leases until `default_lease_ttl`, but when `max_lease_ttl` is reached the client **does not rotate credentials automatically** — the application can lose database connectivity without an explicit recovery path (documented community behavior; see Spring Cloud Vault lease lifecycle and reference implementations such as `ivangfr/springboot-vault-examples`).

We need a VaultSpring-native approach to generate, renew, and (when required) rotate PostgreSQL credentials **without application restart**, while keeping the Render/env path from ADR-0002 unchanged.

## Decision drivers

- Reduce exposure window for leaked credentials (temporal least privilege).
- Avoid silent failure at `max_lease_ttl` without rotation.
- Preserve compatibility with profiles `vault` / `prod-vault` and the env-based production path.
- No restart as the primary rotation strategy.
- Effort aligned with current deployment (Docker Compose / Render), not a dedicated Kubernetes platform team.

## Options considered

### A — Keep static KV v2 (status quo)

- Pros: zero implementation cost; matches current `application-vault.yml`.
- Cons: no TTL; does not use Vault dynamic secrets.

### B — Database Secrets Engine + Spring Cloud Vault lease lifecycle

- Enable Vault `database` engine for PostgreSQL; configure `spring.cloud.vault.database` and `spring.cloud.vault.config.lifecycle`.
- Pros: short-lived credentials; renewal until `default_lease_ttl`; revocation on shutdown.
- Cons: does not alone handle terminal `max_lease_ttl` expiry.

### C — B + runtime DataSource rotation

- Add a lease-expiry listener and safe pool replacement (HikariCP or routing wrapper) without process restart.
- Pros: covers renewal and terminal expiry without downtime.
- Cons: higher complexity; integration tests for lease expiration required.

### D — Vault Agent sidecar (file/env injection)

- Pros: decouples secret lifecycle from the JVM.
- Cons: requires Kubernetes or equivalent orchestration; out of scope for current Compose/Render layout.

## Decision

Adopt **Option C** as the target, delivered in two phases:

1. **Phase 1:** Option B — migrate Vault-backed PostgreSQL from static KV v2 to the Database Secrets Engine with lease lifecycle enabled (`min-renewal`, `expiry-threshold`). Document in `docs/configuration.md`. Validate required Maven artifacts for database backend support on Spring Cloud Vault 2025.0.x before coding.
2. **Phase 2:** Option C — implement lease-expiry handling and safe `DataSource` / connection-pool rotation for `max_lease_ttl`.

**Option D** is deferred until a Kubernetes deployment model exists.

**ADR-0002 remains in force** for profile routing: Render/production continues to use `SPRING_DATASOURCE_*`; only the Vault profile path evolves from static KV to dynamic database credentials.

## Consequences

### Positive

- Database credentials gain TTL and automatic revocation.
- Terminal lease expiry can be handled without restart (after Phase 2).
- Aligns product positioning with Vault-native dynamic secrets.

### Negative / trade-offs

- Operational overhead: Vault `database/roles/vaultspring-app`, PostgreSQL admin credential for the engine, dev Compose seed/init updates.
- Integration tests must cover lease renewal and (Phase 2) expiration with reduced `max_lease_ttl` in test environments.
- `scripts/vault-seed-dev.sh` and local docs must distinguish KV bootstrap from database engine setup.

## Follow-up

- Open a `[feat]` GitHub issue; branch from `sandbox`; implement per [PKB prompt](../prompts/by-domain/security/dynamic-postgresql-credentials.v1.md).
- Update `docs/configuration.md` and `docs/architecture.md` in the implementation PR.
- Mark this ADR **Accepted** when Phase 1 merges; note Phase 2 status in the PR or a follow-up issue.

## References

- [ADR-0002](./0002-datasource-via-vault-or-env.md)
- [Spring Cloud Vault — Database backend](https://docs.spring.io/spring-cloud-vault/docs/current/reference/html/#vault.config.backends.database)
- [Spring Cloud Vault — Lease lifecycle](https://docs.spring.io/spring-cloud-vault/docs/current/reference/html/#vault.config.lifecycle)
- [HashiCorp Vault — Database secrets engine](https://developer.hashicorp.com/vault/docs/secrets/databases)

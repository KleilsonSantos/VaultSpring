# ADR-0003: SecurityFilterChain baseline before JWT login

## Status

Accepted (baseline merged; JWT Bearer login delivered in 0.1.x — [#6](https://github.com/KleilsonSantos/VaultSpring/issues/6))

## Context

Actuator and API need consistent HTTP security before JWT is implemented. Spring Security is on the classpath (`spring-boot-starter-security`).

## Decision

Ship `SecurityConfig` with:

- Public: `/actuator/health`, `/api/v1/**`, Swagger paths (dev)
- Authenticated: `/actuator/prometheus`, `/actuator/info`
- CSRF off, stateless sessions, CORS on `/api/**`, HSTS in `prod`
- `PasswordEncoder` BCrypt bean in the same config class

JWT extends this chain (delivered in #6) — it did not replace the filter-chain structure documented here.

## Alternatives considered

- Spring Security disabled until JWT — rejected: leaves Prometheus open
- JWT first — rejected: larger slice; baseline needed for IT and actuator

## Consequences

- Positive: testable via `SecurityFilterChainTest`; CI integration-tests pass
- Negative: baseline shipped with public API until JWT (#6); now `/api/v1/**` requires Bearer except login

See [architecture.md](../architecture.md#security-model-current).

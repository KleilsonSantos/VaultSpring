# ADR-0003: SecurityFilterChain baseline before JWT login

## Status

Accepted (JWT tracked in [#6](https://github.com/KleilsonSantos/VaultSpring/issues/6))

## Context

Actuator and API need consistent HTTP security before JWT is implemented. Spring Security is on the classpath (`spring-boot-starter-security`).

## Decision

Ship `SecurityConfig` with:

- Public: `/actuator/health`, `/api/v1/**`, Swagger paths (dev)
- Authenticated: `/actuator/prometheus`, `/actuator/info`
- CSRF off, stateless sessions, CORS on `/api/**`, HSTS in `prod`
- `PasswordEncoder` BCrypt bean in the same config class

JWT will extend this chain in #6 — not replace undocumented ad-hoc rules.

## Alternatives considered

- Spring Security disabled until JWT — rejected: leaves Prometheus open
- JWT first — rejected: larger slice; baseline needed for IT and actuator

## Consequences

- Positive: testable via `SecurityFilterChainTest`; CI integration-tests pass
- Negative: API is public until JWT lands (documented, not hidden)

See [architecture.md](../architecture.md#security-model-current).

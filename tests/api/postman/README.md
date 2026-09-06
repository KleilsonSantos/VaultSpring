# VaultSpring — Postman API tests

Postman Collections complement (do **not** replace) the canonical quality gates:

| Layer | Tool | Blocking for merge? |
| ----- | ---- | ------------------- |
| Unit / functional (embedded) | `./mvnw test` (`ApiFunctionalAuditTest`, etc.) | **Yes** |
| Integration | `bash scripts/run-integration-tests.sh` | **Yes** when API/persistence touched |
| Live smoke (curl) | `bash scripts/api-live-smoke.sh` | **Yes** when audit requires live proof (`ok infra`) |
| **Postman** | Collection Runner / CLI | **No** — regression & manual audit; CI optional |

Postman artifacts mirror [`docs/api.md`](../../../docs/api.md) and [`scripts/api-live-smoke.sh`](../../../scripts/api-live-smoke.sh). Missing or outdated Collections **must not** block commits or PR merge if Maven tests and documented smoke script pass.

## Layout

```text
tests/api/postman/
  collections/VaultSpring - Smoke Tests.postman_collection.json
  environments/VaultSpring - Local.postman_environment.json
  README.md
```

Future (optional): Functional, Negative, Regression collections — add only when endpoints grow beyond smoke scope.

## Prerequisites

- App running: `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` or `docker compose up app`
- Seed user: `john@example.com` / `secret123` (Flyway V3)

## Import and run

1. Postman → Import → collection + environment
2. Select **VaultSpring - Local**
3. Collection Runner → **VaultSpring - Smoke Tests** → Run

## Traceability

| Test ID | Request | Source |
| ------- | ------- | ------ |
| SMOKE-001 | GET /actuator/health | docs/api.md, api-live-smoke.sh |
| SMOKE-002 | GET /actuator/prometheus (401) | SecurityConfig, ProblemDetail |
| SMOKE-003 | POST /api/v1/auth/login | AuthController |
| SMOKE-004 | GET /api/v1/users (401) | SecurityConfig |
| SMOKE-005 | GET /api/v1/users (200) | UserController |

## CI (optional, not enabled)

Postman CLI (`postman collection run`) may be wired in GitHub Actions later. Until then, CI uses Maven + Testcontainers only.

## Cross-repo note

The PKB asset [`prompt.delivery.aios-postman-api-suite`](../../../docs/prompts/by-domain/delivery/aios-postman-api-suite.v1.md) targets **AIOS**, not VaultSpring. Do not mix collections between repositories.

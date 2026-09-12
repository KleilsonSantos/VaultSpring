# VaultSpring — Copilot instructions (always-on)

Authoritative routing: [`AGENTS.md`](../AGENTS.md). If instructions conflict with code, **`pom.xml` and source win**.

## Stack (do not invent)

- Java **17**, Spring Boot **3.5.x** (not Boot 4 in drive-by changes)
- PostgreSQL 15, Flyway, HashiCorp Vault Database Secrets Engine (Spring Cloud Vault Config)
- Spring Security + JWT Bearer (`POST /api/v1/auth/login`), RBAC on `/api/v1/users`
- Namespace: `io.github.kleilsonsantos.security.vaultspring`

## Git and delivery

- Branch from **`sandbox`**: `feature/*`, `fix/*`, `docs/*`, `chore/*`
- Work PR → **`sandbox`** (`Refs #N`); promote PR → **`main`** (`Closes #N`)
- Conventional Commits — **no gitmoji** ([`docs/guides/writing-style.md`](../docs/guides/writing-style.md))
- Commit only when the human asks
- Never add `Co-authored-by: Cursor`, Copilot, or `cursoragent@cursor.com` ([`docs/guides/attribution.md`](../docs/guides/attribution.md))

## Secrets and security

- Never commit `.env`, Vault tokens, unseal keys, or `vault/data/`
- `.env.example` holds **local placeholders only** — production uses env or Vault
- `prod`/`hom` profiles fail fast on weak JWT secrets ([`JwtSecretGuard`](../src/main/java/io/github/kleilsonsantos/security/vaultspring/config/JwtSecretGuard.java))

## Quality gates (Java changes)

1. `./mvnw -B checkstyle:check test`
2. `bash scripts/pre-push-check.sh` before push
3. Live Docker/JVM only after unit tests green and owner **`ok infra`**

## Custom agents

Specialized agents live in [`.github/agents/`](agents/). Map: [`docs/guides/github-agents.md`](../docs/guides/github-agents.md).

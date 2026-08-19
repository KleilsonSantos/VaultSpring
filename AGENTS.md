# AGENTS.md — VaultSpring

Lightweight pointer for Cursor, GitHub Copilot, and other tools that auto-load `AGENTS.md`.

## Mission

VaultSpring is a **Spring Boot** service for **secure secret management** with PostgreSQL, Flyway, Docker, and HashiCorp Vault as infrastructure. Do not invent layers (MapStruct, Spring Cloud Vault client, Spring Security filter chain) that are not in `pom.xml`.

## Source order

1. **Code** — `src/main/java`, `src/main/resources`, `pom.xml`
2. **Runtime config** — `application*.yml`, `docker-compose.yml`, `Dockerfile`
3. **Delivery** — `.github/`, `Makefile`, `scripts/`
4. **Docs** — `README.md`, `HELP.md`, `CHANGELOG.md`, `CONTRIBUTING.md`, `SECURITY.md`

If a summary conflicts with `pom.xml` or source, the code wins.

## Task routing

- **Git, PR, CI**: `CONTRIBUTING.md`, `.github/pull_request_template.md`, `.github/workflows/maven.yml`
- **AppSec / secrets**: `SECURITY.md`, `CHECKLISTAPPSEC.md` (checklist only — do not add exploit PoCs)
- **Quality gates**: `.cursor/rules/quality-gates.mdc`, Checkstyle, JaCoCo, CodeQL, Sonar on `main`

## Hard constraints

- Java 17 + Spring Boot 3.5.x (OSS line ended 2026-06-30; last patch **3.5.16**). Do not jump to Spring Boot 4 in a drive-by change.
- Conventional Commits (`feat`, `fix`, `docs`, `ci`, `chore`, `refactor`, `test`). No gitmoji requirement.
- Commit only when the human asks.
- Never commit `.env`, Vault unseal keys, or `vault/data/`.
- Default Git flow: semantic branch from `main` → PR → `main`. Do not introduce an AIOS `sandbox` branch unless the owner asks.

## Owner cadence

`next` = proposal only.  
`ok` / `prossegue` = implement the accepted slice.

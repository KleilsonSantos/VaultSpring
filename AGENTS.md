# AGENTS.md — VaultSpring

Lightweight pointer for Cursor, GitHub Copilot, and other tools that auto-load `AGENTS.md`.

## Mission

VaultSpring is a **Spring Boot** service for **secure secret management** with PostgreSQL, Flyway, Docker, and HashiCorp Vault. **Spring Cloud Vault Config** (`spring-cloud-starter-vault-config`, train 2025.0.x) is on the classpath for the `vault` profile. Do not invent other Cloud starters or full Spring Security unless they are in `pom.xml`.

## Source order

1. **Code** — `src/main/java`, `src/main/resources`, `pom.xml`
2. **Runtime config** — `application*.yml`, `docker-compose.yml`, `Dockerfile`
3. **Delivery** — `.github/`, `Makefile`, `scripts/`
4. **Docs** — `README.md`, `HELP.md`, `CHANGELOG.md`, `CONTRIBUTING.md`, `SECURITY.md`

If a summary conflicts with `pom.xml` or source, the code wins.

## Task routing

- **Git, issues, PR, releases**: `docs/guides/git-workflow.md`, `docs/guides/task-kickoff.md`, `docs/guides/releases.md`, `CONTRIBUTING.md`, `.github/pull_request_template.md`
- **Kickoff script**: `scripts/task-kickoff.sh <issue> <branch>`
- **AppSec / secrets**: `SECURITY.md`, `CHECKLISTAPPSEC.md` (checklist only — do not add exploit PoCs)
- **Quality gates**: `.cursor/rules/quality-gates.mdc`, Checkstyle, JaCoCo, CodeQL, Sonar on `main`
- **AIOS reference platform**: [ai-operating-system](https://github.com/KleilsonSantos/ai-operating-system) (governance patterns; no `sandbox` branch here)

## Hard constraints

- Java 17 + Spring Boot 3.5.x (OSS line ended 2026-06-30; last patch **3.5.16**). Do not jump to Spring Boot 4 in a drive-by change.
- Conventional Commits (`feat`, `fix`, `docs`, `ci`, `chore`, `refactor`, `test`). No gitmoji requirement.
- **Traceability**: new implementation work starts with a GitHub issue; PR body includes `Closes #N`.
- Commit only when the human asks.
- Never commit `.env`, Vault unseal keys, or `vault/data/`.
- Default Git flow: semantic branch from `main` → PR → `main`. Do not introduce an AIOS `sandbox` branch unless the owner asks.

## Owner cadence

`next` = proposal only.  
`ok` / `prossegue` = implement the accepted slice.

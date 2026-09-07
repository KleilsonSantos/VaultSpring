# AGENTS.md — VaultSpring

Lightweight pointer for AI coding assistants (and humans) that auto-load `AGENTS.md`.

## Attribution (required)

- **Author / owner:** Kleilson Santos — `kleilson@icloud.com` — [KleilsonSantos](https://github.com/KleilsonSantos)
- **Never** add `Co-authored-by: Cursor`, Copilot, or `cursoragent@cursor.com` to commits
- **Never** append “Made with Cursor” (or similar) to PR descriptions
- Details: [`docs/guides/attribution.md`](docs/guides/attribution.md)

## Mission

VaultSpring is a **Spring Boot** service for **secure secret management** with PostgreSQL, Flyway, Docker, and HashiCorp Vault. **Spring Cloud Vault Config** and **`spring-boot-starter-security`** with **JWT Bearer** (`POST /api/v1/auth/login`, issue #6) are on the classpath. Do not invent other Cloud starters or MapStruct unless they are in `pom.xml`.

## Source order

1. **Code** — `src/main/java`, `src/main/resources`, `pom.xml`
2. **Runtime config** — `application*.yml`, `docker-compose.yml`, `Dockerfile`
3. **Delivery** — `.github/`, `Makefile`, `scripts/`
4. **Docs** — `docs/README.md`, `README.md`, `HELP.md`, `CHANGELOG.md`, `CONTRIBUTING.md`, `SECURITY.md`
5. **Prompt knowledge base** — `docs/prompts/` (catalog only; policies in guides/rules win over long prompts)

If a summary conflicts with `pom.xml` or source, the code wins.

## Task routing

- **Git, issues, PR, releases, attribution**: `docs/guides/attribution.md`, `docs/guides/delivery-automation.md`, `docs/guides/git-workflow.md`, `docs/guides/task-kickoff.md`, `docs/guides/releases.md`, `docs/README.md`, `CONTRIBUTING.md`, `.github/pull_request_template.md`
- **Kickoff script**: `scripts/task-kickoff.sh <issue> <branch>`
- **AppSec / secrets**: `SECURITY.md`, `CHECKLISTAPPSEC.md`; comprehensive audit — PKB `prompt.security.comprehensive-appsec-audit`; JWT — `prompt.security.jwt-login-review`; dynamic PostgreSQL — [ADR-0005](docs/adr/0005-dynamic-postgresql-credentials-vault.md), PKB `prompt.security.dynamic-postgresql-credentials`
- **Quality gates**: `.cursor/rules/quality-gates.mdc`, Checkstyle, JaCoCo, CodeQL, Sonar on `main`
- **Local runtime (MacBook)**: [`docs/guides/local-runtime-authorization.md`](docs/guides/local-runtime-authorization.md), `.cursor/rules/local-runtime-gate.mdc` — **order:** inspect → audit → unit tests green → **`ok infra`** (if live needed) → live proof → commit-ready → commit only when you ask
- **PKB intake** (`PKB intake` / `catalogar prompt` / `guardar prompt`): catalog into `docs/prompts/` per [`docs/prompts/README.md`](docs/prompts/README.md). Do **not** run the prompt unless the owner also says `ok` / `prossegue`. Validate with `bash scripts/check-pkb-inventory.sh`.
- **AIOS reference platform**: [ai-operating-system](https://github.com/KleilsonSantos/ai-operating-system) (governance patterns; **`sandbox` + `main`** — [ADR-0004](docs/adr/0004-git-branching-strategy-sandbox.md))

## Hard constraints

- Java 17 + Spring Boot 3.5.x (OSS line ended 2026-06-30; last patch **3.5.16**). Do not jump to Spring Boot 4 in a drive-by change.
- Conventional Commits (`feat`, `fix`, `docs`, `ci`, `chore`, `refactor`, `test`). **No gitmoji** — see [`docs/guides/writing-style.md`](docs/guides/writing-style.md).
- **Traceability**: new implementation work starts with a GitHub issue; PR body includes `Closes #N`.
- Commit only when the human asks.
- Never commit `.env`, Vault unseal keys, or `vault/data/`.
- Default Git flow: semantic branch from **`sandbox`** → PR → **`sandbox`** → PR → **`main`**. Bootstrap: `bash scripts/bootstrap-sandbox.sh` if remote `sandbox` is missing.

## Owner cadence

`next` = proposal only (inspect/audit; no implement, no infra, no commit).  
`ok` / `prossegue` = implement + unit tests (steps 3–4).  
`ok infra` / `autorizo infra` / `prossegue infra` = live Docker/JVM/Vault **only after unit tests pass** (steps 5–6).  
Explicit **commit** request = step 8, **only after** audit/tests (and live proof when required) succeed. Never commit automatically on "ready to commit".

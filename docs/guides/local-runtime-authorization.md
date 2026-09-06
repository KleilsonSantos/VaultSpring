# Local runtime authorization (MacBook resource gate)

Protects the owner's **shared MacBook** from agents or scripts starting heavy runtime services without explicit approval.

Policies in this guide and `.cursor/rules/local-runtime-gate.mdc` **win** over long PKB prompts when they conflict.

## Canonical delivery order (mandatory)

Agents and contributors follow this sequence. **Do not skip or reorder steps.**

```text
1. INSPECT     read-only — code, docs, git, docker ps, compose config, lsof
       ↓
2. AUDIT       map architecture, endpoints, gaps (classify OBSERVADO / NÃO VALIDADO)
       ↓
3. TASK GATE   owner: ok / prossegue — implement fixes if in scope
       ↓
4. UNIT PROOF  ./mvnw -B checkstyle:check test → BUILD SUCCESS (required)
       ↓
5. INFRA GATE  only if live/Docker/Vault/E2E still needed — owner: ok infra / autorizo infra
       ↓
6. LIVE PROOF  minimal Compose/JVM, curl/smoke, IT with Testcontainers → record status + body
       ↓
7. COMMIT READY report success, diff summary, suggested Conventional Commit messages
       ↓
8. COMMIT      only when owner explicitly asks to commit (never automatic)
```

**Rules:**

- **Step 5 never before step 4.** Do not request `ok infra` until unit tests (and any in-scope fixes) pass.
- **Step 8 never before steps 4–6.** Do not commit until audit scope is green: unit tests mandatory; live proof mandatory when the task required infra.
- If live proof is **not** needed, step 5–6 may be skipped and marked `N/A` in the report — still require step 4 before commit-ready.
- If step 6 fails, return to step 3 (fix) and re-run 4 → 5 → 6; do not commit.

## Three gates (do not merge)

| Gate | Owner says | When | Agent may |
| ---- | ---------- | ---- | --------- |
| **Task** | `ok` · `prossegue` | After inspect/audit plan accepted | Implement, docs, run unit tests (H2 / Surefire) |
| **Infra** | `ok infra` · `autorizo infra` · `prossegue infra` | **After step 4 success**, if live proof needed | Start local Docker/JVM/Vault runtime (see below) |
| **Commit** | explicit commit request | **After steps 4–6 success** | `git add` + `git commit` (Conventional Commits) |

PKB catalog intake remains catalog-only until task gate. Infra gate is **never** a substitute for unit test success.

## Read-only (steps 1–2, no infra gate)

- Read repo, docs, `pom.xml`, Compose files, configs
- `git status`, `git diff`, `git log`
- `./mvnw -B checkstyle:check test` (step 4 — allowed under task gate without infra)
- `docker ps`, `docker network ls`, `docker volume ls`, `docker images`
- `docker compose config` (render only — **do not** `up`)
- `lsof -nP -iTCP -sTCP:LISTEN`, `colima status` (no `colima start`)

Classify Docker/Vault/E2E live results as `NÃO VALIDADO` until steps 5–6 complete.

## Requires infra gate (step 5+)

Ask the owner **only after step 4 passes**:

| Category | Examples |
| -------- | -------- |
| Container runtime | `colima start`, `docker compose up`, `docker run`, `docker build`, `docker pull` |
| Application JVM | `./mvnw spring-boot:run`, `java -jar`, background app on `:8080` |
| Process control | `kill` / `pkill` on service ports |
| Integration tests | `./mvnw verify -Pintegration-tests` (Testcontainers) |
| Vault ops | init, unseal, `scripts/vault-seed-dev.sh` on live Vault |
| Live HTTP proof | `curl`, Swagger, `scripts/api-live-smoke.sh` against agent-started stack |

Never: `docker compose down -v`, `docker system prune`, stopping unrelated containers.

## Pre-infra brief (required at step 5)

Include evidence that **step 4 passed** (test count, BUILD SUCCESS), then:

1. **Objective** — what live proof is still missing
2. **Services to start** — minimal set (e.g. postgres only)
3. **Ports** — vs `lsof` / `docker ps`
4. **Coexistence** — reuse vs isolate
5. **Resource note** — Colima CPU/RAM, long-running JVM
6. **Destructive risk** — kills, volume writes
7. **Stop plan** — what stays running after session

Then ask:

> Unit tests pass. Autoriza subir a infraestrutura descrita para validação live? Responda `ok infra` / `autorizo infra` / `prossegue infra`.

## Commit-ready checklist (step 7)

Before stating "ready to commit", confirm:

- [ ] Audit scope executed and reported
- [ ] `./mvnw -B checkstyle:check test` — BUILD SUCCESS
- [ ] Live proof done or explicitly `N/A` with justification
- [ ] CHANGELOG / docs updated when behavior changed
- [ ] No secrets in diff (`.env`, tokens, unseal keys)
- [ ] Suggested commit message(s) — Conventional Commits, one concern per commit

Wait for owner to say **commit** — do not commit on "ready to commit" alone.

## Owner cadence (summary)

| Phrase | Meaning |
| ------ | ------- |
| `next` | Proposal only — stop at step 2 |
| `ok` / `prossegue` | Steps 3–4 (implement + unit tests) |
| `ok infra` / `autorizo infra` / `prossegue infra` | Steps 5–6 (after step 4 green) |
| "commita" / "commit" / explicit ask | Step 8 |
| Decline or silence on infra | Report with live items `NÃO VALIDADO`; no commit unless owner accepts gap |

## References

- Local setup: [`../development.md`](../development.md)
- Docker topology: [`../../docker-compose.yml`](../../docker-compose.yml)
- Live smoke: `bash scripts/api-live-smoke.sh`
- PKB audit: [`../prompts/by-domain/spring-boot/integral-e2e-vault-docker-audit.v1.md`](../prompts/by-domain/spring-boot/integral-e2e-vault-docker-audit.v1.md)

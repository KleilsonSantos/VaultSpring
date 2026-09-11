# Portfolio Strategy Base — Kleilson Santos

**Owner:** KleilsonSantos (`https://github.com/KleilsonSantos`)  
**Canonical Maven/Java namespace:** `io.github.kleilsonsantos`  
**Portfolio site:** https://kleilson-portfolio.pages.dev  
**Document purpose:** Single source of context to reopen work **inside each real repo** (not this IdeaProjects folder).  
**Last updated:** 2026-09-11 (America/Sao_Paulo) — §6.2 synced with VaultSpring `main`

---

## 0. Critical working rule (read first)

### Agree: do NOT drive Tier A work from `IdeaProjects`

This folder (`/Users/kleilson/Downloads/IdeaProjects`) is mostly **old local learning labs** (hexagonal/Kafka/loja demos). It does **not** carry the AI-guided engineering surface of your real GitHub projects:

| Capability lost here | Present in real repos |
| --- | --- |
| `AGENTS.md` / Copilot instructions | Yes (VaultSpring, AIOS, cloud-event-lab, portfolio…) |
| Cursor rules / project prompts | Yes (per-repo `.cursor`, `docs/prompts`, agents) |
| Git workflow (sandbox → main, scopes) | Documented per repo |
| CI/CD + security gates | GitHub Actions per repo |
| ADRs / architecture SSOT | `docs/adr/` per repo |
| Domain language / ownership cadence | Per-repo guides |

**Therefore:**

1. Use **this document** as the strategic base.  
2. Open **Cursor/IDE on each target repository root** separately.  
3. Let that repo’s `AGENTS.md` + CI + ADRs guide implementation.  
4. Treat IdeaProjects as optional private lab only — **do not publish** those demos as portfolio.

**Suggested local clones (outside IdeaProjects):**

```text
~/code/ai-operating-system
~/code/VaultSpring
~/code/cloud-event-lab
~/code/kleilson-portfolio
~/code/aios-companion   # supporting
```

Temporary clones from this session (can delete after use):

```text
IdeaProjects/_VaultSpring-ns-migrate
IdeaProjects/_cloud-event-lab
```

---

## 1. Professional positioning

### Target profile

```text
Senior Backend Engineer
+ AppSec / DevSecOps
+ Cloud / Distributed Systems
+ AI-Assisted / AI-Native Software Engineering
+ SDLC Automation
```

### Desired GitHub narrative (5-minute Tech Lead read)

> Backend + DevSecOps engineer who also builds **AI governance for the SDLC** — with ADRs, CI, releases, and security evidence — not “many Spring demos”.

### Public story triangle (must be visible)

```text
AI-SDLC          →  ai-operating-system
AppSec / Java    →  VaultSpring
Cloud / events   →  cloud-event-lab   (next deepen)
Storefront       →  kleilson-portfolio
```

### Gap to avoid claiming publicly

- Bio mentions Banking / Microservices.
- Public `banking-*` were **empty** → made **private** (2026-09-10).
- `purchase-*` is **private** (stronger than IdeaProjects loja, but not public evidence yet).
- Until a polished public store/banking slice exists: **lead with VaultSpring + AIOS + cloud-event-lab**.

### Suggested GitHub bio (copy/paste — adjust tone if needed)

```text
Senior Backend · AppSec/DevSecOps · Cloud · AI-assisted SDLC

Building AI governance for delivery (ai-operating-system) and secure Java backends
(VaultSpring: Vault DB secrets, JWT/RBAC, observability). Cloud events lab in progress.

Portfolio → kleilson-portfolio.pages.dev
```

Short variant (160 chars):

```text
Backend + AppSec engineer · AI-SDLC (AIOS) · secure Java (VaultSpring) · cloud events lab · kleilson-portfolio.pages.dev
```

---

## 2. Namespace & naming (global English)

### Maven / Java (no personal domain required)

Official path (Sonatype/Maven Central):

```text
github.com/KleilsonSantos  →  io.github.kleilsonsantos
```

| Family | groupId | Example package |
| --- | --- | --- |
| Root | `io.github.kleilsonsantos` | — |
| Security | `io.github.kleilsonsantos.security` | `…security.vaultspring` |
| AIOS | `io.github.kleilsonsantos.aios` | (align when touching coords) |
| Messaging / events | `io.github.kleilsonsantos.messaging` | target for cloud-event-lab |
| Store | `io.github.kleilsonsantos.store` | purchase-* when public |
| Portfolio | npm/TS scopes as already used | kleilson-portfolio |

### English repo / artifact style

- kebab-case, capability-oriented  
- Avoid: `my-project-*`, `foo/bar`, course brands (`arantes`, `udemy`), Portuguese public names  

Examples:

| Avoid | Prefer |
| --- | --- |
| my-hexagonal | customer-onboarding-service (if ever published) |
| microservice-lojavirtual | virtual-store |
| venda-ingressos | ticket-sales-producer |

---

## 3. What was already done (this engagement)

| Action | Status | Evidence |
| --- | --- | --- |
| IdeaProjects portfolio audit | Done | Canvases + chat |
| Naming / English scheme | Done | Strategy agreed |
| Namespace choice | Done | `io.github.kleilsonsantos` |
| GitHub × IdeaProjects map | Done | Tier A is on GitHub |
| 8 empty `banking-*` → private | Done | size=0 shells hidden |
| Archive 22 old tutorial repos | Done | DIO/generics/bootcamp/etc. |
| VaultSpring RBAC (SEC-001) | **Released** | `v0.1.7` — PR #104/#105, closes #103 |
| VaultSpring coords migration | **Merged** | PR #110 → `io.github.kleilsonsantos.security` / `…vaultspring` |
| VaultSpring post-release cycle | **Merged** | PR #107 → `0.1.8-SNAPSHOT` on `main` |
| VaultSpring sandbox sync | **Pending** | PR main → sandbox (after namespace + release) |
| Profile pins | **Pending (manual)** | GitHub UI — see §3 checklist |
| cloud-event-lab deepen | **Next implementation** | Start inside that repo |

### Manual pin checklist (GitHub UI)

https://github.com/KleilsonSantos → **Customize your pins**:

1. `ai-operating-system`  
2. `VaultSpring`  
3. `cloud-event-lab`  
4. `kleilson-portfolio`  

---

## 4. Standout ranking (public evidence)

Scores = audit judgment on ADRs / CI / tests / releases / structure (not stars).

| Rank | Repo | Score | Role | Open IDE here? |
| ---: | --- | ---: | --- | --- |
| 1 | [ai-operating-system](https://github.com/KleilsonSantos/ai-operating-system) | 92 | **HIGHLIGHT** — AI-SDLC platform | **Yes** |
| 2 | [VaultSpring](https://github.com/KleilsonSantos/VaultSpring) | 90 | **HIGHLIGHT** — AppSec Java (`v0.1.7` RBAC + namespace) | **Yes** |
| 3 | [kleilson-portfolio](https://github.com/KleilsonSantos/kleilson-portfolio) | 78 | **HUB** — storefront | **Yes** |
| 4 | [cloud-event-lab](https://github.com/KleilsonSantos/cloud-event-lab) | 72 | **RISING** — cloud architecture | **Yes (next)** |
| 5 | [aios-companion](https://github.com/KleilsonSantos/aios-companion) | 65 | SUPPORT | Optional |

### Do not lead with

| Repo | Why |
| --- | --- |
| Mongo-RestFull-API | Tests exist; weak naming; no ADRs; many open issues |
| OrderProcessingSystem | Typical Spring demo |
| appsec-daily-hub | Content site, not systems proof |
| IdeaProjects labs | Educational / superseded |
| banking-* (private empty) | No public evidence |
| Archived DIO/bootcamp set | Noise by design |

---

## 5. Best path forward (agreed)

**Pins (you) + deepen `cloud-event-lab` + do not fake public banking yet.**

### Why this path

- AI proof already strong → AIOS  
- AppSec proof already strong → VaultSpring  
- Missing public credible **cloud/distributed** chapter → cloud-event-lab has ADRs/CI but thin test/release evidence  
- `purchase-*` public slice = high value, high effort → **later**

### Sequence (2–3 weeks)

```text
Week 0 (you, 15 min)
  → Set pins
  → Align bio with public evidence

Week 1–2 (work IN cloud-event-lab repo)
  → Testcontainers (Postgres) for API integration tests
  → One-command local demo script (compose + health + sample curl)
  → README: clear EN “what / why / quick start / architecture” (keep honesty on Phase status)
  → Release v0.1.0 when demo-grade

Week 2–3 (parallel / optional)
  → VaultSpring Dependabot triage (PRs #108, #109 open on sandbox — review majors)
  → Merge sandbox sync PR when green
  → Archive remaining weak publics (OrderProcessingSystem, microservices-system, api-rest-springboot, nodejs_jwt, …)

Month+ 
  → Public purchase/store slice only if narrative still needs microservices proof
```

---

## 6. Per-project playbooks (open each repo in IDE)

### 6.1 `ai-operating-system` — keep #1 sharp

**Open:** repo root (has `AGENTS.md`, ADRs, MCP, pipeline).

**Proves:** AI as SDLC orchestrator / governance — not chatbot CRUD.

**Evidence already:** ~32 ADRs, ~41 tests, releases (e.g. v0.48.x), CI, SECURITY, CONTRIBUTING.

**Do next (inside that repo’s AI flow):**

- Ensure 5-minute README clarity for Tech Leads  
- Link Companion + portfolio  
- Avoid diluting with unrelated features  

**Do not:** rebuild AIOS logic from IdeaProjects.

---

### 6.2 `VaultSpring` — AppSec showcase

**Open:** VaultSpring root.

**Proves:** Secure Java delivery (Vault dynamic DB secrets, JWT/RBAC, observability, quality gates, semver releases).

**Shipped (public evidence on `main`):**

| Item | Evidence |
| --- | --- |
| Latest release | **`v0.1.7`** (2026-09-08) — User API RBAC, `GET /api/v1/users/me`, Flyway `V4` |
| Namespace | `io.github.kleilsonsantos.security` / `io.github.kleilsonsantos.security.vaultspring` (PR #110) |
| Dev cycle | `0.1.8-SNAPSHOT` (PR #107 merged) |
| Security story | ADR-0005 Vault DB creds + rotation, SEC-001 RBAC, JWT, Checkstyle/JaCoCo/CodeQL CI |

**Do next (inside repo):**

- Merge **sandbox ← main** sync PR when CI green (sandbox was behind post-#110)  
- Triage Dependabot PRs **#108** (logstash encoder 9.x), **#109** (springdoc 3.x — major)  
- Optional ADR for coordinate migration (`com.vaultspring` → `io.github.kleilsonsantos.security`)  
- Keep README one-liner aligned with `v0.1.7` capabilities  

**Do not:** re-open RBAC or release work unless a new issue exists.

**Follow:** `AGENTS.md`, `docs/guides/*`, sandbox→main workflow.

---

### 6.3 `cloud-event-lab` — NEXT IMPLEMENTATION FOCUS

**Open:** cloud-event-lab root (**not** IdeaProjects).

**Proves:** Cloud-native event lab with honest multi-cloud roadmap.

**Current (observed):**

- Modular monolith Java 21 + React  
- ADRs + architecture docs strong  
- Compose Postgres (+ LocalStack/Azurite profiles)  
- CI: tests + Gitleaks + Trivy + ShellCheck  
- Tests: domain unit + MockMvc IT on **H2** (`application-test.yml`) — **no Testcontainers yet**  
- groupId today: `dev.kleilson` (consider align later to `io.github.kleilsonsantos.messaging`)  
- Git flow: branch from **`sandbox`** → PR → sandbox → main  
- **No release tag yet**

**Executable PR checklist (do in-repo with AGENTS.md):**

1. `test(api): Postgres Testcontainers for EventApi IT`  
2. `feat(deploy): one-command local demo script` (`scripts/demo-local.sh` or similar)  
3. `docs(readme): English-first quick start + architecture pointer` (keep Portuguese owner notes if needed in `owner/`)  
4. `chore(release): v0.1.0` when local demo + tests green  
5. Optional later: `chore(api): migrate groupId to io.github.kleilsonsantos.messaging`

**Local quick start (already in README):**

```bash
./scripts/preflight.sh
cd deploy/compose && docker compose up -d postgres && cd ../..
cd apps/api && mvn spring-boot:run
# other terminal
cd apps/web && npm install && npm run dev
curl -u lab:lab-change-me -H 'Content-Type: application/json' \
  -d '{"type":"order.created","source":"curl","idempotencyKey":"demo-1","payloadJson":"{\"ok\":true}"}' \
  http://localhost:8080/api/events
```

---

### 6.4 `kleilson-portfolio` — hub

**Open:** portfolio monorepo root.

**Role:** Professional storefront; content must stay verifiable (CV/GitHub/LinkedIn only).

**Do next:** Ensure highlighted projects on the site match pins (AIOS, VaultSpring, cloud-event-lab).

---

### 6.5 `aios-companion` — satellite

Keep as related product to AIOS (ADR-0014 boundary). Not a third unrelated highlight.

---

## 7. IdeaProjects disposition

| Local area | Action |
| --- | --- |
| hexagonal/*, kafka/*, microservice-lojavirtual, swagger, jwt, logging… | **Private lab only** — superseded by GitHub Tier A |
| foo, java17, redis stub, empty wiremock | Ignore / delete locally |
| Do not push raw labs to public GitHub | Hard rule |

If any ArchUnit idea from `my-hexagonal` is useful, **port deliberately** into a real repo under that repo’s PR process — do not publish the lab.

---

## 8. Canvases created in Cursor (reference)

Located under Cursor project canvases (IDE side panel), not inside GitHub repos:

- `portfolio-engineering-audit.canvas.tsx`  
- `naming-conventions-audit.canvas.tsx`  
- `english-naming-scheme.canvas.tsx`  
- `github-vs-ideaprojects.canvas.tsx`  
- `execution-plans.canvas.tsx`  
- `standout-projects-analysis.canvas.tsx`  

This markdown file is the **portable base** to copy into each repo’s `owner/` or keep in a private notes vault.

---

## 9. Decision log (short)

| Decision | Choice |
| --- | --- |
| Domain without owning DNS | `io.github.kleilsonsantos` |
| Public Tier A | AIOS + VaultSpring (+ cloud-event-lab rising) |
| Empty banking shells | Private |
| Old course repos | Archived (22) |
| Best ROI next | cloud-event-lab demo-grade inside its own IDE/AI flow |
| IdeaProjects as daily driver for Tier A | **No** |

---

## 10. How to resume in a new Cursor window

1. Open **one** repo: e.g. `cloud-event-lab`.  
2. Attach or paste **this document** (or link to it).  
3. Say: *Follow AGENTS.md + PORTFOLIO_STRATEGY_BASE §6.3 — implement Testcontainers + demo script + README EN; PR to sandbox.*  
4. Do not ask the agent to “fix everything from IdeaProjects”.

---

## 11. One-line agreement

**Yes — working only in IdeaProjects loses the AI-guided GitHub/CI/ADR toolchain of each real project. Strategy lives here; execution lives inside each repository root.**

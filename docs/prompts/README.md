# Prompt Knowledge Base (PKB)

Docs-as-Code catalog of **reusable prompt assets** for VaultSpring — security reviews, documentation audits, delivery checklists, and Spring Boot engineering patterns.

This is **not** a second product SSOT. Canonical truth remains:

1. **Code** — `src/main/java`, `src/main/resources`, `pom.xml`
2. **Architecture / ADRs** — `docs/architecture.md`, `docs/adr/`
3. **Governance** — `AGENTS.md`, `.cursor/rules/`, `docs/guides/`
4. **Policies > long prompts** — link guides and ADRs instead of pasting rules into prompt bodies

**MacBook infra gate (canonical):** [`../guides/local-runtime-authorization.md`](../guides/local-runtime-authorization.md) — order: unit tests green → `ok infra` → live proof → commit-ready → commit when owner asks. Task gate alone is not enough for infra or commit.

Reference platform: [AI Operating System](https://github.com/KleilsonSantos/ai-operating-system) (`docs/prompts/`). VaultSpring adapts the PKB pattern without AIOS runtime engines (no `compilePrompt`, no vector index in MVP).

Long-term ladder: [`pkb-evolution.md`](./pkb-evolution.md).

## Layout

```text
docs/prompts/
  README.md                 # rules + intake (this file)
  pkb-evolution.md          # evolution ladder (Markdown-first MVP)
  index.yaml                # machine-readable catalog
  by-domain/<domain>/<slug>.vN.md
  templates/                # skeletons for new assets
  archived/                 # retired versions (keep history)
```

Naming: `{slug}.v{n}.md` — descriptive English kebab slug, integer version `n`, stable `id` in frontmatter and `index.yaml`.

## Domains (VaultSpring)

| Domain | Use for |
| ------ | ------- |
| `documentation` | README/docs hub audits, docs-as-code alignment |
| `security` | JWT, AppSec checklist runs, Vault posture (no exploit PoCs) |
| `spring-boot` | API, Flyway, Testcontainers, configuration reviews |
| `delivery` | Release readiness, SemVer, CI/delivery automation |
| `templates` | Skeletons only — promote to `by-domain/` when ready |

Do **not** invent a new domain without owner approval.

## Frontmatter (required)

| Field | Notes |
| ----- | ----- |
| `id` | Stable unique id, e.g. `prompt.security.jwt-login-review` |
| `title` | Short English title |
| `domain` | Folder under `by-domain/` |
| `purpose` | One-line why |
| `tags` | Searchable keywords |
| `version` | Integer matching `.vN` |
| `status` | `active` · `draft` · `archived` |
| `language` | Body language (`pt-BR`, `en-US`, …) |
| `ai_ready` | Safe to feed agents as-is |
| `related_docs` | Paths under repo root |
| `related_prompts` | Other prompt `id`s |
| `created_at` / `updated_at` | ISO date (`YYYY-MM-DD`) |

## Chat trigger (PKB intake)

When the owner pastes an engineering prompt and says any of:

```text
PKB intake
catalogar prompt
guardar prompt
```

treat that as **catalog only**, not “run the analysis now”.

1. Do **not** echo the full prompt back in chat.
2. Do **not** execute the prompt body unless the owner also says `ok` / `prossegue`.
3. Live Docker/JVM/Vault/E2E inside a prompt still requires **`ok infra`** per [`../guides/local-runtime-authorization.md`](../guides/local-runtime-authorization.md).
4. Pick an existing domain (table above). Do not invent a new domain without asking.
5. Name the file `{kebab-slug}.v1.md` (bump `.vN` only when the contract forks).
6. `id`: `prompt.<domain>.<slug>`.
7. Fill required frontmatter + register the row in [`index.yaml`](./index.yaml).
8. Add a short **catalog note** if a related ADR, issue, or guide already exists — link it; do not duplicate results.
9. Keep the chat reply to: id, path, domain, duplicate status.
10. Commit / PR only when the owner asks. Branch from `main` (`docs/*` or `feature/*`), PR → `main` (no `sandbox` branch).

**Excluded:** one-off chat dumps, vendor skill packs copied wholesale, prompts that only repeat `AGENTS.md` / `.cursor/rules`, auto-persisting every chat message.

## Inventory check

```bash
bash scripts/check-pkb-inventory.sh
```

Verifies `index.yaml` paths exist and frontmatter `id` / `domain` / `version` align with filenames.

## Related

- [`../README.md`](../README.md) — documentation hub
- [`../../AGENTS.md`](../../AGENTS.md) — agent routing and source order
- [`../guides/local-runtime-authorization.md`](../guides/local-runtime-authorization.md) — MacBook infra gate
- [`../guides/writing-style.md`](../guides/writing-style.md) — no gitmoji in delivery artifacts

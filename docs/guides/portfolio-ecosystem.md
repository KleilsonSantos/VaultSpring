# Portfolio ecosystem

VaultSpring is a **standalone** Spring Boot service. It does **not** depend on any other repository at build or runtime.

Delivery governance (branching, traceability, PKB layout) was **adapted** from the [AI Operating System (AIOS)](https://github.com/KleilsonSantos/ai-operating-system) reference platform — same portfolio, independent repos.

## What is local (SSOT)

| Topic | VaultSpring canonical doc |
| ----- | ------------------------- |
| Branch strategy | [ADR-0004](../adr/0004-git-branching-strategy-sandbox.md) |
| Git flow | [git-workflow.md](./git-workflow.md) |
| Releases / SemVer | [releases.md](./releases.md) |
| Task kickoff | [task-kickoff.md](./task-kickoff.md) |
| Attribution | [attribution.md](./attribution.md) |
| Delivery automation | [delivery-automation.md](./delivery-automation.md) |
| Writing style | [writing-style.md](./writing-style.md) |
| PKB | [prompts/README.md](../prompts/README.md) |

When a guide and AIOS disagree, **VaultSpring docs and ADRs win**.

## What was adapted from AIOS

| Pattern | VaultSpring choice |
| ------- | ------------------ |
| `sandbox` + `main` two-stage promotion | Same — [ADR-0004](../adr/0004-git-branching-strategy-sandbox.md) |
| Issue → branch → PR traceability | Same — `scripts/task-kickoff.sh`, issue-link CI |
| PKB (`docs/prompts/`, `index.yaml`) | Same layout; **no** `compilePrompt`, **no** vector index (MVP) |
| Commits | **No gitmoji** — Conventional Commits only ([writing-style.md](./writing-style.md)) |
| Postman suite prompt | Cross-repo asset targets **AIOS**, not VaultSpring — see [prompt.delivery.aios-postman-api-suite](../prompts/by-domain/delivery/aios-postman-api-suite.v1.md) |

## AIOS origin links (maintained here)

| Topic | AIOS source |
| ----- | ----------- |
| Branching ADR | [ADR-0002 — git branching](https://github.com/KleilsonSantos/ai-operating-system/blob/main/docs/adr/0002-git-branching-strategy.md) |
| Git workflow | [git-workflow.md](https://github.com/KleilsonSantos/ai-operating-system/blob/main/docs/guides/git-workflow.md) |
| Releases | [releases.md](https://github.com/KleilsonSantos/ai-operating-system/blob/main/docs/guides/releases.md) |
| Task kickoff | [task-kickoff.md](https://github.com/KleilsonSantos/ai-operating-system/blob/main/docs/guides/task-kickoff.md) |
| PKB evolution | [pkb-evolution.md](https://github.com/KleilsonSantos/ai-operating-system/blob/main/docs/prompts/pkb-evolution.md) |

Guides in this repo link here instead of repeating AIOS URLs in every footer.

## Related

- [`CONTRIBUTING.md`](../../CONTRIBUTING.md)
- [`AGENTS.md`](../../AGENTS.md)

# Architecture Decision Records (ADR)

Lightweight **MADR-style** records: one decision per file, versioned in Git, reviewed in PRs.

| ID | Decision | Status |
| -- | -------- | ------ |
| [0001](./0001-record-architecture-decisions.md) | Use ADRs in this repo | Accepted |
| [0002](./0002-datasource-via-vault-or-env.md) | Datasource: Vault KV v2 **or** env vars | Accepted |
| [0003](./0003-security-filter-chain-before-jwt.md) | SecurityFilterChain baseline before JWT (#6) | Accepted |

When a decision is superseded, add a new ADR and mark the old one **Superseded by ADR-NNNN** — do not delete history.

References: [MADR](https://adr.github.io/madr/), [C4 model](https://c4model.com/), [architecture.md](../architecture.md).

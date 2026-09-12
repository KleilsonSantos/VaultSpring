# PKB — Long-term vision (VaultSpring)

The Prompt Knowledge Base starts as an **organized Docs-as-Code collection**. Metadata and layout are shaped so later stages can plug in without a wholesale rewrite.

## Evolution ladder

1. **Organized Markdown + `index.yaml`** ← **MVP (this repo)**
2. **Inventory script** — `scripts/check-pkb-inventory.sh` (drift: missing files, orphan assets)
3. **Textual / tag search** — `grep`, `rg`, or IDE search over frontmatter and `index.yaml`
4. **Optional ADR** — semantic search / embeddings (defer until catalog grows)
5. **Cross-repo reference** — link or invoke AIOS MCP `aios_search_pkb` for shared patterns (optional)
6. **Agent composition** — `.github/agents/` pick catalog entries by domain + tags

## Design principles

- **KISS / YAGNI** — no vector DB until justified by ADR and catalog size
- **Docs-as-Code** — reviewable in PRs like any other doc
- **DRY** — link `docs/adr/`, `docs/guides/`, `pom.xml`; do not paste governance into every prompt
- **Clean boundaries** — PKB feeds agents; it does not replace code or architecture SSOT

## VaultSpring vs AIOS

| AIOS | VaultSpring |
| ---- | ----------- |
| `engines/documentation` audit | `scripts/check-pkb-inventory.sh` |
| MCP `aios_search_pkb` | Git search + `index.yaml` (MVP) |
| Domains: `ai-engineering`, … | Domains: `security`, `spring-boot`, `delivery`, … |
| Policy Engine JSON | `AGENTS.md` + `.cursor/rules/` |

Source pattern: [portfolio-ecosystem.md](../guides/portfolio-ecosystem.md) (AIOS PKB evolution link).

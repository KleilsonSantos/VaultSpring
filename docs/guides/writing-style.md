# Writing style — VaultSpring

Normas de texto para **commits, issues, PRs, CHANGELOG e documentação técnica**. Seguir sempre; referências abaixo explicam o porquê.

## Regra resumida

| Camada | Emoji / gitmoji | Onde |
| ------ | ----------------- | ---- |
| **Entrega** | **Não** | Commits, títulos de issue/PR, `CHANGELOG.md`, `docs/**`, `HELP.md`, guias |
| **Marketing** | Mínimo | `README.md` — badges Shield OK; evitar listas com ✅/🚀 |

## Por que **sem gitmoji** nos commits

1. **[Conventional Commits](https://www.conventionalcommits.org/)** — tipo no prefixo (`feat:`, `fix:`), parseável por SemVer, hooks e `check-semver-alignment.sh`. Gitmoji duplica semântica e quebra tooling que lê o tipo literal.
2. **VaultSpring / AIOS adaptado** — [AIOS](https://github.com/KleilsonSantos/ai-operating-system) usa gitmoji; este repo **optou por não usar** ([`CONTRIBUTING.md`](../../CONTRIBUTING.md), [`git-workflow.md`](./git-workflow.md)) para alinhar hooks e Dependabot (`chore(deps):`).
3. **Automação** — `scripts/check-semver-alignment.sh` classifica release pelo prefixo `feat|fix|perf`; emoji no subject atrapalha classificação e busca no histórico.

## Por que **sem emoji** na documentação técnica

1. **[Keep a Changelog](https://keepachangelog.com/)** — entradas factuais, sem decoração; facilita diff e release notes.
2. **Documentation as Code** — `docs/` versionado em PRs; texto neutro funciona em GitHub, IDE, export PDF e leitores de tela ([WCAG — texto claro](https://www.w3.org/WAI/WCAG22/Understanding/use-of-color.html): não depender de símbolos para significado).
3. **Consistência com ecossistema Java/Spring** — docs oficiais ([Spring Boot Reference](https://docs.spring.io/spring-boot/reference/), [HashiCorp Vault](https://developer.hashicorp.com/vault/docs)) usam prosa e headings sem gitmoji.
4. **Agentes de IA** — `AGENTS.md` e `.github/agents/` parseiam Markdown; headings com emoji reduzem qualidade de busca e citação.

## Issues e PRs

- Título: **`[feat]`**, **`[fix]`**, **`[docs]`**, **`[ci]`**, **`[chore]`**, **`[epic]`** + descrição curta — ver templates em [`.github/ISSUE_TEMPLATE/`](../../.github/ISSUE_TEMPLATE/).
- Corpo: seções `## Summary`, acceptance criteria, test plan — sem “Made with …” ([`attribution.md`](./attribution.md)).

## README.md

- **Permitido:** badges [Shields.io](https://shields.io/) (versão, build, licença) — padrão de mercado, não são gitmoji.
- **Evitar:** emoji em headings (`## 🚀 …`), listas com ✅ em massa, GIFs que substituem conteúdo técnico.
- **Preferir:** link para [`docs/README.md`](../README.md) como fonte técnica principal.

## Checklists (ex.: `CHECKLISTAPPSEC.md`)

- Use `- [ ]` / `- [x]` GitHub task lists — sem emoji no título das seções.
- Referencie código real (`SecurityConfig`, issue #6) — não inventar endpoints.

## Referências internas

- [`git-workflow.md`](./git-workflow.md) — commits, branches, **granularity**
- [`attribution.md`](./attribution.md) — autoria Kleilson Santos
- [`releases.md`](./releases.md) — CHANGELOG
- [`CONTRIBUTING.md`](../../CONTRIBUTING.md)

## Referências externas

| Referência | Uso |
| ---------- | --- |
| [Conventional Commits v1.0](https://www.conventionalcommits.org/en/v1.0.0/) | Formato de commit |
| [Keep a Changelog 1.1](https://keepachangelog.com/en/1.1.0/) | `CHANGELOG.md` |
| [GitHub Markdown spec](https://github.github.com/gfm/) | Task lists, headings |
| [AIOS git-workflow](https://github.com/KleilsonSantos/ai-operating-system/blob/main/docs/guides/git-workflow.md) | Governança de referência (gitmoji **não** replicado aqui) |

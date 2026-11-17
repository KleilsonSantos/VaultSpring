# CLAUDE.md — VaultSpring

Ponteiro leve para o Claude Code. Não é fonte de verdade: comece por `AGENTS.md`
(ordem de fontes, hard constraints e ritual `next`/`ok` já definidos lá) e pelos
guias em `docs/guides/`. Se este arquivo divergir do `AGENTS.md` ou do código,
eles vencem.

## Atribuição

Nunca adicionar linha de co-autoria de ferramenta de IA (Claude incluído, além
de Cursor/Copilot) em commits ou PRs — autor único: Kleilson Santos. Ver
`docs/guides/attribution.md`.

## Ritual do dono

Antes de qualquer mudança não trivial: `next` (proposta — inspecionar/auditar,
sem implementar) → aguardar `ok`/`prossegue` (implementa + testes unitários) →
`ok infra`/`autorizo infra` somente depois dos testes unitários passarem →
commit somente quando pedido explicitamente.

## MCP

`.mcp.json` deste projeto configura o `github-mcp-server` remoto (OAuth, sem
token em arquivo) para leitura/gestão de issues e PRs, alinhado ao fluxo de
rastreabilidade (issue → PR → `Closes #N`) do `AGENTS.md`.

Não expor endpoints do VaultSpring como ferramentas MCP sem autorização
explícita do dono — este é um serviço de gestão de segredos, e o
`CHECKLISTAPPSEC.md` ainda tem itens de hardening em aberto.

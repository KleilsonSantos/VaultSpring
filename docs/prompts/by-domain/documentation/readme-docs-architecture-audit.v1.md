---
id: prompt.documentation.readme-docs-architecture-audit
title: README and documentation hub audit
domain: documentation
purpose: Audit README vs docs hub role; verify landing-page vs deep docs split without inventing APIs
tags: [documentation, readme, architecture-audit, docs-as-code, vaultspring]
version: 1
status: active
language: pt-BR
ai_ready: true
related_docs:
  - README.md
  - docs/README.md
  - docs/architecture.md
  - CONTRIBUTING.md
related_prompts:
  - prompt.documentation.docs-code-alignment-audit
created_at: 2026-09-06
updated_at: 2026-09-06
---

# Prompt — Auditoria do README e hub `docs/`

## Objetivo

Analisar se a documentação do VaultSpring segue Docs-as-Code e se o `README.md` funciona como **landing page**, delegando profundidade para `docs/`.

## Constraints

- **Código vence** — validar claims contra `pom.xml` e `src/main/java`
- Não inventar starters, MapStruct ou features ausentes no repo
- Sem gitmoji — ver `docs/guides/writing-style.md`
- Reutilizar estrutura existente (`docs/adr/`, `docs/guides/`, Mermaid)

## Etapas

1. **Inventário** — `README.md`, `docs/README.md`, `HELP.md`, `CHANGELOG.md`, `AGENTS.md`, `SECURITY.md`, `CHECKLISTAPPSEC.md`, ADRs, guides
2. **Papel do README** — onboarding, badges, links para hub; identificar conteúdo que deveria migrar para `docs/`
3. **Hub `docs/`** — navegação, diagramas, gaps vs código real (JWT #6, Vault, CI)
4. **Duplicação** — mesma informação em dois lugares; responsabilidade única por arquivo
5. **Comparação** — padrões OSS (Keep a Changelog, MADR, C4); referência opcional: [ai-operating-system/docs](https://github.com/KleilsonSantos/ai-operating-system/tree/main/docs)
6. **Plano incremental** — issues sugeridas, prioridade, sem big-bang

## Deliverable

1. Pontos fortes e gaps
2. Tabela arquivo → responsabilidade recomendada
3. Lista de issues/epics opcionais (`[docs]` prefix)
4. Nenhuma implementação — só análise, salvo `ok` / `prossegue` do owner

> **Catalog note:** Hub técnico entregue em PR #58. Re-executar só para refresh pós-mudanças grandes.

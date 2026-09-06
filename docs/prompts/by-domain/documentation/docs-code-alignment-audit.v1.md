---
id: prompt.documentation.docs-code-alignment-audit
title: Documentation vs code alignment audit
domain: documentation
purpose: Find doc drift against pom.xml, source, and CI workflows
tags: [documentation, alignment, pom-xml, ci, stack-truth]
version: 1
status: active
language: pt-BR
ai_ready: true
related_docs:
  - pom.xml
  - docs/architecture.md
  - docs/configuration.md
  - docs/api.md
  - AGENTS.md
related_prompts:
  - prompt.documentation.readme-docs-architecture-audit
created_at: 2026-09-06
updated_at: 2026-09-06
---

# Prompt — Auditoria docs ↔ código

## Objetivo

Detectar documentação que **contradiz** o repositório (versões, dependências, endpoints, perfis Spring, jobs CI).

## Constraints

- Ordem de verdade: `pom.xml` → source → `application*.yml` → docs
- Não marcar como “faltando” o que está explicitamente **planned** (ex.: issue #6 JWT) se estiver rotulado como pendente
- Referências a Spring Boot 4 só no epic #33 — não recomendar upgrade drive-by

## Etapas

1. Extrair stack de `pom.xml` (Boot, Cloud, springdoc, Testcontainers, Security)
2. Conferir `docs/architecture.md`, `configuration.md`, `api.md`, `README.md`, `AGENTS.md`
3. Conferir `.github/workflows/` vs `docs/guides/delivery-automation.md`
4. Listar **drift** (doc diz X, código diz Y) com path e linha quando possível
5. Listar **lacunas** (código tem X, doc não menciona)
6. Propor correções mínimas por arquivo

## Deliverable

| Tipo | Path | Evidência | Ação sugerida |
| ---- | ---- | --------- | -------------- |
| drift / gap / OK | … | … | … |

Sem commits — owner decide `ok` / `prossegue` para implementar.

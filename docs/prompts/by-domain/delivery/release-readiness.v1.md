---
id: prompt.delivery.release-readiness
title: Release readiness and SemVer gate review
domain: delivery
purpose: Verify pom, CHANGELOG, tag, and CI SemVer gate before cutting a release
tags: [delivery, semver, changelog, release, ci]
version: 1
status: active
language: pt-BR
ai_ready: true
related_docs:
  - docs/guides/releases.md
  - docs/guides/delivery-automation.md
  - scripts/check-semver-alignment.sh
  - CHANGELOG.md
related_prompts: []
created_at: 2026-09-06
updated_at: 2026-09-06
---

# Prompt — Release readiness (SemVer)

## Objetivo

Validar se o repositório está pronto para PR `chore: release vX.Y.Z`, tag anotada e GitHub Release workflow.

## Constraints

- Fluxo: slices mergeados em `main` → PR release dedicado → tag `vX.Y.Z` → **não** `gh release create` manual se `release.yml` ativo
- SNAPSHOT em `main` é ciclo dev — ver fix SemVer gate (#66)
- Conventional Commits, sem gitmoji

## Etapas

1. `git log` desde última tag `v*.*.*` — classificar commits releaseable vs não
2. `bash scripts/check-semver-alignment.sh HEAD`
3. Conferir `[Unreleased]` vs commits reais em `CHANGELOG.md`
4. Conferir `pom.xml` version (release vs SNAPSHOT pós-tag)
5. CI verde em `main`
6. Checklist PR release + follow-up `X.Y.(Z+1)-SNAPSHOT`

## Deliverable

- Versão recomendada (patch/minor) com justificativa SemVer
- Bloqueadores vs OK
- Sequência exata de comandos/branches (sem executar sem `ok` / `prossegue`)

> **Catalog note:** Release v0.1.4 concluída (#60). Usar para próximo ciclo (`0.1.5+`).

---
id: prompt.templates.blank-task
title: Blank task prompt template
domain: templates
purpose: Skeleton for a new PKB asset — replace fields before promoting to by-domain/
tags: [template, skeleton]
version: 1
status: draft
language: en-US
ai_ready: false
related_docs:
  - docs/prompts/README.md
related_prompts: []
created_at: 2026-09-06
updated_at: 2026-09-06
---

# Prompt — \<short English title\>

## Objective

\<One paragraph: what the agent must achieve\>

## Constraints

- Source order: code (`pom.xml`, `src/`) wins over docs — see `AGENTS.md`
- Do not invent dependencies not in `pom.xml`
- Link `docs/guides/` and ADRs instead of pasting governance
- Minimal change (`no-overengineering`)
- No gitmoji in commits, issues, or PR text
- No exploit PoCs or offensive payloads

## Steps

1. …
2. …

## Deliverable

\<Expected output shape — analysis, issue draft, checklist, or PR plan\>

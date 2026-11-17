---
id: prompt.observability.actuator-metrics-alert-coverage
title: Actuator metrics and alert coverage review
domain: observability
purpose: Review whether a diff touching Actuator exposure, metrics, or tracing has matching alert/dashboard coverage
tags: [observability, actuator, prometheus, grafana, otel, alerting]
version: 1
status: active
language: pt-BR
ai_ready: true
related_docs:
  - ../../../../.github/agents/observability-reviewer.agent.md
  - ../../../../docker/observability/prometheus/rules/alerts.yml
  - ../../../../docker/observability/grafana/dashboards/vaultspring-overview.json
  - ../../../../src/main/resources/application.yml
related_prompts: []
created_at: 2026-11-07
updated_at: 2026-11-07
---

## Purpose

Catalog entry pointing at the runtime observability checklist already enforced in CI. Does not duplicate the checklist body.

## When to use

A diff changes `management.*` in `application*.yml`, adds/changes a metric, or touches `docker/observability/**`.

## Run

Follow `.github/agents/observability-reviewer.agent.md` literally — it is the canonical checklist (also wired into `.github/workflows/claude-code-review.yml`). This entry exists only so the asset is discoverable from the PKB index alongside other reusable review prompts.

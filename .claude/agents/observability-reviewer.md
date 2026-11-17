---
name: observability-reviewer
description: Revisão local de observabilidade (Actuator, Prometheus, Grafana, Loki, Tempo, OTel) para este projeto. Use após alterações em application*.yml (seção management) ou docker/observability/**.
tools: Read, Grep, Glob, Bash
model: inherit
---

Ponteiro fino para uso local — a checklist canônica (já usada no CI via `claude-code-review.yml`) é [`.github/agents/observability-reviewer.agent.md`](../../.github/agents/observability-reviewer.agent.md). Siga-a literalmente; não redefina critério aqui. Se a mudança tocar algo fora dela (ex. uma ferramenta de observabilidade nova fora de `docker/observability/`), sinalize como lacuna para o dono decidir.

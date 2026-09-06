---
id: prompt.security.jwt-login-review
title: JWT login and SecurityFilterChain review
domain: security
purpose: Review JWT implementation against issue #6 acceptance criteria and AppSec baseline
tags: [security, jwt, spring-security, actuator, issue-6]
version: 1
status: active
language: pt-BR
ai_ready: true
related_docs:
  - docs/adr/0003-security-filter-chain-before-jwt.md
  - docs/api.md
  - SECURITY.md
  - CHECKLISTAPPSEC.md
related_prompts: []
created_at: 2026-09-06
updated_at: 2026-09-06
---

# Prompt — Revisão JWT login (#6)

## Objetivo

Revisar a implementação JWT contra os critérios de aceite da issue #6 e baseline ADR-0003.

## Constraints

- Sem PoCs de exploit ou payloads ofensivos
- Segredo JWT via env (`VAULTSPRING_JWT_SECRET`) — nunca no Git
- Out of scope: OAuth2/OIDC externo, refresh token (issue futura)
- Verificar o que **existe** em `pom.xml` — não assumir libs não declaradas

## Checklist

- [ ] `POST /api/v1/auth/login` retorna JWT assinado (HS256 ou docado)
- [ ] `/api/v1/**` exige Bearer exceto login (+ health público conforme ADR)
- [ ] `/actuator/prometheus` e `/actuator/info` usam JWT (não Basic)
- [ ] Propriedades em `application*.yml` + `.env.example`
- [ ] Testes MockMvc / IT: 401 sem token, 200 com token válido
- [ ] OpenAPI Bearer scheme (perfil dev)
- [ ] `CHANGELOG.md` `[Unreleased]` se aplicável

## Etapas

1. Ler `SecurityConfig`, filtros/JWT services, `AuthController`
2. Mapear rotas públicas vs autenticadas
3. Revisar testes (`SecurityFilterChainTest`, `UserApiIT`)
4. Cruzar com `CHECKLISTAPPSEC.md` (seção JWT)
5. Listar findings: blocker / should-fix / nit

## Deliverable

Findings numerados + sugestão de PR/commits semânticos. Implementar só com `ok` / `prossegue`.

> **Catalog note:** Implementação em andamento na branch `feature/6-jwt-login`. Re-executar após merge para review final.

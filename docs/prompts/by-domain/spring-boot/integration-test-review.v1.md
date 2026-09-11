---
id: prompt.spring-boot.integration-test-review
title: Integration test and Testcontainers review
domain: spring-boot
purpose: Review IT patterns, profiles, and CI integration-tests job alignment
tags: [spring-boot, testcontainers, flyway, integration-tests, ci]
version: 1
status: active
language: pt-BR
ai_ready: true
related_docs:
  - docs/development.md
  - src/test/java/io/github/kleilsonsantos/security/vaultspring/UserApiIT.java
  - .github/workflows/maven.yml
related_prompts: []
created_at: 2026-09-06
updated_at: 2026-09-06
---

# Prompt — Revisão de testes de integração

## Objetivo

Avaliar qualidade e sustentabilidade dos testes IT (Testcontainers, Flyway, perfil `it`, job CI).

## Constraints

- `./mvnw -B verify -Pintegration-tests` requer Docker
- Não adicionar dependências fora do `pom.xml` existente
- JWT (#6): ITs devem autenticar quando API estiver protegida

## Etapas

1. Ler `UserApiIT`, `application-it.yml`, profile Maven `integration-tests`
2. Verificar `@ServiceConnection`, Postgres 15, migrations Flyway
3. Conferir job `integration-tests` em `maven.yml` e `release.yml`
4. Identificar gaps de cobertura (auth, erros 409, actuator)
5. Propor casos IT adicionais **somente** se agregarem valor real

## Deliverable

- Estado atual (OK / gap)
- Lista priorizada de melhorias
- Riscos (flakiness, tempo CI, secrets em test)

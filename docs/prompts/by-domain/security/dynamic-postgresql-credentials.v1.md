---
id: prompt.security.dynamic-postgresql-credentials
title: Dynamic PostgreSQL credentials via Vault Database Secrets Engine
domain: security
purpose: Implement or review Vault dynamic DB credentials (Phase 1 lifecycle, Phase 2 pool rotation) per ADR-0005
tags:
  - security
  - vault
  - postgresql
  - dynamic-secrets
  - spring-cloud-vault
  - lease-lifecycle
  - testcontainers
version: 1
status: active
language: pt-BR
ai_ready: true
related_docs:
  - docs/adr/0005-dynamic-postgresql-credentials-vault.md
  - docs/adr/0002-datasource-via-vault-or-env.md
  - docs/configuration.md
  - docs/architecture.md
  - src/main/resources/application-vault.yml
  - pom.xml
  - docs/guides/local-runtime-authorization.md
  - docs/guides/git-workflow.md
related_prompts:
  - prompt.spring-boot.integral-e2e-vault-docker-audit
  - prompt.spring-boot.integration-test-review
created_at: 2026-09-07
updated_at: 2026-09-07
---

> **Catalog note:** Decisão em [ADR-0005](../../../adr/0005-dynamic-postgresql-credentials-vault.md). [ADR-0002](../../../adr/0002-datasource-via-vault-or-env.md) permanece válido para path env/Render. Auditoria Vault ampla: `prompt.spring-boot.integral-e2e-vault-docker-audit` — não duplicar aqui.

# Prompt — Credenciais PostgreSQL dinâmicas (Vault)

## Objetivo

Implementar ou revisar a evolução do path Vault (`vault`, `prod-vault`) de KV v2 estático para **Database Secrets Engine** com lifecycle de lease, e (Fase 2) rotação runtime do pool JDBC — conforme ADR-0005.

## Constraints

- Fonte de verdade: `pom.xml` e código — hoje só `spring-cloud-starter-vault-config`; validar artefato Maven para database backend antes de implementar.
- **Não** alterar path Render/prod env (`SPRING_DATASOURCE_*`) — ADR-0002.
- Sem `@VaultPropertySource` — o repo usa `spring.config.import: vault://` + YAML.
- API oficial: `spring.cloud.vault.database` (não inventar propriedades depreciadas).
- Sem segredos no Git; sem root token em produção.
- Sem PoCs de exploit.
- Ordem MacBook: unit tests → `bash scripts/pre-push-check.sh` → `ok infra` (Vault live) → live proof → commit só quando o owner pedir.
- Não executar implementação até `ok` / `prossegue`; catalog intake sozinho não autoriza código.

## Phase 1 — Database engine + lifecycle

### Vault (dev/Compose)

- [ ] `vault secrets enable database` (se ausente)
- [ ] `database/config/…` apontando para PostgreSQL do Compose
- [ ] Role `vaultspring-app` com `default_ttl` / `max_ttl` documentados
- [ ] Atualizar ou estender seed/init dev (`scripts/vault-seed-dev.sh` ou script dedicado)

### Spring (`application-vault.yml` ou profile dedicado)

```yaml
spring:
  config:
    import: vault://
  cloud:
    vault:
      uri: ${VAULT_ADDR:http://127.0.0.1:8200}
      authentication: TOKEN
      token: ${VAULT_TOKEN:}
      database:
        enabled: true
        role: vaultspring-app
        backend: database
        username-property: spring.datasource.username
        password-property: spring.datasource.password
      config:
        lifecycle:
          enabled: true
          min-renewal: 10s
          expiry-threshold: 1m
          lease-endpoints: SysLeases
          lease-strategy: RetainOnError
```

### Verificação

- [ ] App sobe com profile `vault` / `prod-vault` e conecta ao Postgres
- [ ] Credencial gerada difere da estática KV anterior
- [ ] Lease renova antes de expirar (`default_ttl`)
- [ ] IT (Testcontainers Vault + Postgres ou stack Compose): create → use → renew
- [ ] `docs/configuration.md` + `docs/architecture.md` atualizados
- [ ] `CHANGELOG.md` `[Unreleased]` se comportamento mudou

## Phase 2 — Rotação runtime (max_lease_ttl)

- [ ] Listener de expiração / evento de lease terminal
- [ ] Troca segura do `DataSource` / HikariCP (drain pool antigo)
- [ ] IT com `max_ttl` curto (segundos) — app continua sem restart
- [ ] Métrica/log observável em falha de rotação (sem vazar password)

## Out of scope (issues separadas)

- Kubernetes Auth, Vault Agent sidecar, Vault HA, Transit, PKI
- OAuth2/OIDC, JWT RS256
- Spring Boot 4

## Etapas (implementação)

1. Inspecionar `application-vault.yml`, `pom.xml`, `vault-seed-dev.sh`, ADR-0002/0005
2. Validar dependência Maven e docs Spring Cloud Vault 2025.0.x
3. Configurar Vault database engine (dev)
4. Ajustar YAML + testes Fase 1
5. (Fase 2) Componente de rotação + IT expiração
6. Docs + ADR-0005 → **Accepted** na PR de Fase 1 (Fase 2 pode ser issue follow-up)

## Deliverable

- PR(s) semânticos → `sandbox` (`Refs #N`); promote → `main` (`Closes #N`)
- Findings (review mode): blocker / should-fix / nit numerados
- Evidência: `./mvnw -B test`, `pre-push-check.sh`, CI green (`issue-link`, `quality`, `integration-tests`)

## Definition of Done

```text
[ ] Código + config
[ ] Teste automatizado (unit + IT quando aplicável)
[ ] pre-push-check.sh green
[ ] Documentação alinhada ao código
[ ] ADR-0005 status atualizado
[ ] Sem segredos no diff
```

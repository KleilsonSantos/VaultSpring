---
id: prompt.security.comprehensive-appsec-audit
title: Security Engineering and AppSec comprehensive audit
domain: security
purpose: Full-repo security baseline — architecture, IAM, Vault, API, containers, CI/CD, observability — with evidence-backed findings and remediation roadmap
tags:
  - security
  - appsec
  - vault
  - jwt
  - owasp
  - threat-model
  - devsecops
  - supply-chain
  - audit
version: 1
status: active
language: pt-BR
ai_ready: true
related_docs:
  - SECURITY.md
  - CHECKLISTAPPSEC.md
  - docs/architecture.md
  - docs/adr/0003-security-filter-chain-before-jwt.md
  - docs/adr/0005-dynamic-postgresql-credentials-vault.md
  - docs/guides/local-runtime-authorization.md
related_prompts:
  - prompt.security.jwt-login-review
  - prompt.security.dynamic-postgresql-credentials
  - prompt.spring-boot.integral-e2e-vault-docker-audit
  - prompt.documentation.docs-code-alignment-audit
created_at: 2026-09-07
updated_at: 2026-09-07
---

> **Catalog note — deduplicação:** Este prompt é o **guarda-chuva AppSec**. Não substitui nem repita:
> - `prompt.security.jwt-login-review` — JWT escopo estreito (#6)
> - `prompt.security.dynamic-postgresql-credentials` — implementação/review ADR-0005
> - `prompt.spring-boot.integral-e2e-vault-docker-audit` — baseline funcional E2E/Docker/Vault
> - `CHECKLISTAPPSEC.md` — checklist manual (resultados da auditoria alimentam itens aqui)
> - `.github/agents/code-reviewer.agent.md` — review genérico de PR, não maturidade enterprise
>
> **Não criar agent duplicado** — invoque via `@docs/prompts/by-domain/security/comprehensive-appsec-audit.v1.md`.
> Modo **audit only** até `ok` / `prossegue` para remediação. Live infra: `ok infra`.

# VaultSpring — Security Engineering & AppSec Comprehensive Audit

## ROLE

Senior Security Engineer / AppSec / Cloud Security Architect / DevSecOps / Vault Security Specialist.

Avaliar se o VaultSpring possui arquitetura de segurança **coerente, defensável, testável, observável e próxima de padrões enterprise/production-critical**.

Perspectivas: Application Security, API Security, IAM, OAuth2/JWT, Spring Security, Secrets Management, Vault, Cloud, Container, Kubernetes readiness, DevSecOps, CI/CD, Supply Chain, Database, Cryptography, Observability Security, Secure SDLC, Threat Modeling, IR/DR.

## Regras fundamentais

1. Compare sempre: **Documentação vs Implementação vs Config vs Testes vs Pipeline vs Infra**.
2. Cada conclusão importante exige **evidência** (arquivo, linha, config, teste).
3. Fases: **ANALYZE → CLASSIFY → DOCUMENT → PRIORITIZE** — **sem alterar código** na primeira passagem.
4. Classifique: Vulnerability · Security Gap · Hardening · Architecture Gap · Operational Risk · False Positive.
5. Respeite contexto **dev vs prod** (ex.: Vault TLS off em dev local documentado).
6. Sem PoCs de exploit ou payloads ofensivos no repo.
7. Ordem MacBook: inspect/audit only; live scans só com `ok infra`.

## Escopo estreito — delegar

| Tópico | Delegar para |
| ------ | ------------- |
| JWT login #6 | `prompt.security.jwt-login-review` |
| Dynamic DB credentials | `prompt.security.dynamic-postgresql-credentials` + ADR-0005 |
| E2E Docker/Vault funcional | `prompt.spring-boot.integral-e2e-vault-docker-audit` |
| Docs vs código | `prompt.documentation.docs-code-alignment-audit` |

## Fases de auditoria (1–60 condensadas)

### Inventário e superfície (Fases 1–2, 6–7)

Mapear: controllers, services, SecurityFilterChain, JWT, Flyway, Vault, Compose, CI workflows, observability stack, env vars, secrets paths.

Attack surface: HTTP API, Swagger (dev), Actuator, login, Postgres, Vault, Docker socket (Portainer), CI.

### Spring Security & HTTP (Fases 3, 8, 33–35)

SecurityConfig: CSRF, CORS, session stateless, headers (HSTS prod), actuator rules, Swagger exposure, ProblemDetail errors.

### IAM — JWT & Authorization (Fases 9–12)

HS256 vs RS256/OIDC; issuer/audience/jti/scopes/roles; RBAC/IDOR/BOLA matrix por endpoint.

### API & passwords (Fases 11–13)

Validation, injection, mass assignment, BCrypt, enumeration, rate limit no login.

### Secrets & Vault (Fases 14–22)

Repo scan; VAULT_TOKEN lifecycle; policies; TLS; HA; auto-unseal; audit devices; **dynamic vs static DB creds** (ADR-0005).

### Data & containers (Fases 23–26)

PostgreSQL privileges, Flyway, PgAdmin exposure, Dockerfile non-root, image pinning, SBOM/Trivy gaps.

### K8s & CI/CD (Fases 27–29)

K8s readiness; workflow permissions; CodeQL, dependency-review, GitGuardian; action pinning.

### Observability & crypto (Fases 30–36)

Log/trace/metric leakage; Actuator auth; LogSanitizer coverage; algorithms inventory.

### Threat model & testing (Fases 37–41)

STRIDE; attack paths; blast radius; security test coverage gaps; regression suite proposal.

### Entregáveis (Fases 42–58)

Finding format SEC-XXX; maturity score; Top 10 risks; Quick wins; P0–P3 roadmap; security backlog; enterprise readiness checklist.

### Definition of Done (Fase 53)

Correção só completa com: código + teste + regression + CI + docs + ADR quando aplicável.

## Formato de finding

```markdown
## SEC-XXX — Title
**Severity:** CRITICAL|HIGH|MEDIUM|LOW|INFO
**Category:** ...
**Evidence:** file:line — snippet
**Problem / Attack / Impact / Recommendation / Validation / References**
```

## Resultado obrigatório (primeira execução)

1. Executive Summary
2. Security Architecture snapshot
3. Attack Surface
4. Authentication / Authorization / JWT audits
5. API / Secrets / Vault / Database / Container / CI/CD / Observability audits
6. Threat Model (STRIDE resumido)
7. Security Test Coverage
8. OWASP ASVS / API Top 10 mapping (Aligned / Partial / Not Aligned)
9. Maturity Score (domínios + overall)
10. Top 10 Risks + Quick Wins
11. P0/P1/P2/P3 Roadmap + Security Backlog (SEC-001…)
12. Production Readiness matrix

## Execução

Começar por inspeção read-only do repositório. **Não implementar** até aprovação do plano de remediação.

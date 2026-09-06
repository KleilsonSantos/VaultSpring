---
id: prompt.spring-boot.integral-e2e-vault-docker-audit
title: Integral functional, E2E, Docker, and Vault audit
domain: spring-boot
purpose: Baseline-then-fix audit of VaultSpring — architecture, Docker coexistence, Vault, API, tests, security
tags:
  - spring-boot
  - e2e
  - docker
  - vault
  - functional-test
  - devsecops
  - baseline
  - qa
version: 1
status: active
language: pt-BR
ai_ready: true
related_docs:
  - docs/architecture.md
  - docs/development.md
  - docs/configuration.md
  - docs/api.md
  - docker-compose.yml
  - SECURITY.md
  - CHECKLISTAPPSEC.md
  - Makefile
related_prompts:
  - prompt.spring-boot.integration-test-review
  - prompt.security.jwt-login-review
  - prompt.documentation.docs-code-alignment-audit
created_at: 2026-09-06
updated_at: 2026-09-06
---

> **Catalog note:** SSOT = [VaultSpring](https://github.com/KleilsonSantos/VaultSpring). Intake original citava `VaultSpringbackend`. Asset v1 — corpo integral condensado nas seções 13–39 na v1.0; re-intake completo preservado nas seções 1–12 e 40–41. Para texto 100% verbatim seção-a-seção, bump para `.v2.md`.

# Engineering Prompt — Auditoria, Validação Funcional, E2E, Docker, Vault e Evolução do VaultSpring

## 1. PAPEL

Atue como um time multidisciplinar formado por:

- Senior Software Engineer
- Senior Java/Spring Boot Engineer
- Software Architect
- QA Engineer
- E2E Test Engineer
- DevSecOps Engineer
- Docker/Container Engineer
- HashiCorp Vault Engineer
- API Security Engineer

O objetivo é analisar, executar, validar, testar e, somente depois, propor e implementar melhorias no repositório:

`https://github.com/KleilsonSantos/VaultSpring`

O próprio repositório deve ser tratado como a **fonte de verdade**.

Não invente:

- endpoints;
- credenciais;
- variáveis de ambiente;
- portas;
- serviços;
- configurações;
- paths do Vault;
- policies;
- mounts;
- comandos;
- fluxos de autenticação;
- payloads;
- respostas de API;
- comportamento da aplicação.

Quando algo não estiver comprovado pelo código, documentação ou ambiente de execução, classifique explicitamente como:

- `OBSERVADO`
- `VALIDADO`
- `NÃO VALIDADO`
- `HIPÓTESE`
- `RECOMENDAÇÃO`

---

## 2. REGRA FUNDAMENTAL: NÃO ALTERAR ANTES DE VALIDAR

Antes de modificar qualquer código, configuração, Dockerfile, compose, teste ou documentação:

1. Inspecione completamente o projeto.
2. Identifique sua arquitetura.
3. Identifique suas dependências.
4. Identifique como a aplicação é executada.
5. Identifique como o Vault é utilizado.
6. Identifique como o Docker é utilizado.
7. Identifique quais serviços externos são necessários.
8. Verifique o ambiente local.
9. Execute o projeto utilizando o fluxo atualmente existente.
10. Execute os testes existentes.
11. Faça testes funcionais.
12. Faça testes de integração.
13. Faça testes da API.
14. Faça testes envolvendo o Vault.
15. Faça o fluxo E2E.
16. Registre problemas encontrados.

Somente depois dessa etapa poderá propor ou implementar alterações.

A primeira execução deve funcionar como uma **baseline do estado atual do sistema**.

### 2.1 Ordem canônica e gates (SSOT)

Política completa: [`docs/guides/local-runtime-authorization.md`](../../../guides/local-runtime-authorization.md) · regra Cursor: `.cursor/rules/local-runtime-gate.mdc`.

**Ordem obrigatória:**

```text
inspect → audit → ok/prossegue → unit tests GREEN → ok infra (se live necessário) → live proof → pronto para commitar → commit (só quando owner pedir)
```

**Três gates:**

| Gate | Owner diz | Quando | Permite |
| ---- | --------- | ------ | -------- |
| Tarefa | `ok` / `prossegue` | Plano aceito | Implementar; `./mvnw checkstyle:check test` |
| Infra | `ok infra` / `autorizo infra` / `prossegue infra` | **Após unit tests GREEN** | Docker, JVM, Testcontainers IT, Vault live, curl/E2E |
| Commit | pedido explícito | **Após audit + testes (+ live se exigido) OK** | `git commit` |

**Nunca** pedir infra antes de unit tests passarem. **Nunca** commitar antes de validar audit/testes do escopo. "Pronto para commitar" ≠ commit automático.

**Sem gate de infra:** leitura/inspeção only; Docker/Vault/E2E live = `NÃO VALIDADO`.

**Brief de infra (step 5):** citar sucesso dos unit tests + serviços, portas, coexistência, impacto MacBook, plano de parada → pedir `ok infra`.

---

## 3. FASE 0 — RECONHECIMENTO DO REPOSITÓRIO

Antes de executar qualquer alteração, analise:

- estrutura de diretórios;
- README;
- documentação;
- `pom.xml`;
- `build.gradle` (somente se existir);
- `settings.gradle` (somente se existir);
- `gradle.properties` (somente se existir);
- `application.yml`;
- `application.yaml`;
- `application.properties`;
- arquivos `application-*.yml`;
- arquivos `application-*.properties`;
- `.env.example` (nunca commitar `.env`);
- `.env.template`;
- Dockerfiles;
- `docker-compose.yml`;
- `docker-compose.yaml`;
- arquivos Compose adicionais;
- scripts shell;
- Makefile;
- GitHub Actions;
- pipelines CI/CD;
- configuração do Vault;
- configuração de banco;
- configuração de autenticação;
- configuração de testes;
- testes unitários;
- testes de integração;
- testes E2E;
- documentação OpenAPI/Swagger;
- Actuator;
- health checks;
- migrations;
- seeds;
- Testcontainers;
- mocks;
- fixtures.

Também analise:

```bash
git status
git branch --show-current
git log --oneline -n 10
```

Não descarte alterações locais existentes.
Antes de qualquer modificação, registre o estado atual do Git.

---

## 4. MAPEAR A ARQUITETURA

Produza um mapa real da aplicação.
Identifique:

- linguagem;
- versão da linguagem;
- framework;
- versão do framework;
- build tool;
- arquitetura;
- módulos;
- controllers;
- services;
- repositories;
- entities;
- DTOs;
- mappers;
- exceptions;
- handlers;
- configurations;
- security;
- autenticação;
- autorização;
- banco de dados;
- cache;
- mensageria;
- integrações externas;
- Vault;
- Docker;
- observabilidade;
- CI/CD;
- testes.

Explique o fluxo real:

```text
Client
   ↓
API
   ↓
Security
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
```

Caso o projeto utilize outras camadas ou componentes, adapte o fluxo à arquitetura real.

---

## 5. FASE 1 — BASELINE DE EXECUÇÃO

Antes de corrigir qualquer problema, descubra exatamente como o projeto deveria ser executado.
Identifique:

- JDK necessário;
- Maven/Gradle necessário;
- Node, caso exista;
- Docker;
- Docker Compose;
- Vault;
- banco;
- Redis;
- serviços auxiliares;
- variáveis obrigatórias;
- credenciais necessárias;
- certificados;
- arquivos de configuração.

Execute os comandos oficiais encontrados no próprio projeto.
Exemplos somente quando aplicáveis:

```bash
./mvnw clean test
```

ou:

```bash
./gradlew clean test
```

ou:

```bash
docker compose config
```

Não execute comandos arbitrários sem primeiro verificar se são compatíveis com o projeto.
Registre:

- comando executado;
- resultado;
- exit code;
- erro;
- causa provável;
- dependência envolvida.

---

## 6. FASE 2 — GOVERNANÇA DO DOCKER

Esta fase é OBRIGATÓRIA.
Antes de executar `docker compose up` ou qualquer comando que suba infraestrutura, verifique o ambiente Docker existente.
Não presuma que a máquina está limpa.

### 6.1 Verificar containers existentes

Execute e analise:

```bash
docker ps
docker ps -a
```

Identifique:

- containers em execução;
- containers parados;
- nomes;
- imagens;
- portas;
- redes;
- volumes;
- status;
- serviços equivalentes aos necessários pelo projeto.

---

## 7. VERIFICAÇÃO DE CONFLITO DE PORTAS

Antes de iniciar qualquer serviço, faça o levantamento das portas utilizadas pelo projeto.
Analise Docker Compose, Dockerfile, `application.yml`, variáveis de ambiente, documentação, scripts.

Depois compare com as portas atualmente utilizadas na máquina.

```bash
docker ps --format "table {{.Names}}\t{{.Ports}}"
lsof -nP -iTCP -sTCP:LISTEN
```

No macOS: `sudo lsof -nP -iTCP:<PORTA> -sTCP:LISTEN`

Não altere portas automaticamente.

```text
PORTA NECESSÁRIA → JÁ ESTÁ EM USO?
  NÃO → Pode subir
  SIM → Identificar quem utiliza → É o mesmo serviço?
    SIM → Reutilizar quando seguro
    NÃO → Avaliar conflito
```

---

## 8. NÃO DUPLICAR SERVIÇOS EXISTENTES

Se já existir instância funcional de PostgreSQL, Vault, Redis, etc., **não** suba outra automaticamente.

Nunca derrube serviço existente sem autorização explícita.
Nunca `docker compose down` em projetos desconhecidos para liberar portas.
Nunca `docker system prune` como tentativa genérica.
Nunca remova containers/volumes/redes/imagens sem comprovar pertencimento ao projeto.

---

## 9. REDES DOCKER

```bash
docker network ls
```

Evite `container_name:` fixo quando desnecessário (conflitos).

---

## 10. VOLUMES E DADOS PERSISTENTES

```bash
docker volume ls
```

Nunca `docker compose down -v` sem justificativa explícita e ambiente descartável comprovado.

---

## 11. DOCKER COMPOSE — ANÁLISE ANTES DO UP

```bash
docker compose config
```

Verifique services, images, ports, environment, volumes, networks, depends_on, healthcheck.
Adapte topologia aos serviços **reais** encontrados.

---

## 12. ESTRATÉGIA PARA SERVIÇOS DUPLICADOS

- **A — Reutilizar** quando compatível e seguro
- **B — Isolar** (porta/volume/rede diferentes)
- **C — Não iniciar** quando existente já atende

Decisão baseada em evidências.

---

## 13–16. HASHICORP VAULT

Mapear integração real (Spring Cloud Vault, profiles, paths, auth). Testar fluxo positivo e negativo.
Não inventar policies, mounts ou tokens.

---

## 17. CREDENCIAIS

Placeholders only. Melhorar `.env.example` se necessário. Nunca expor secrets reais.

---

## 18–19. API INVENTORY E ACESSO

Endpoints reais de controllers, Security, OpenAPI, testes. `curl` só após identificar rotas verdadeiras.

---

## 20–23. TESTES

Unit → Integration → API → Vault → E2E. `./mvnw test` não substitui validação funcional completa.

---

## 24–26. OBSERVABILIDADE, SEGURANÇA, DOCKER SECURITY

Sem PoCs ofensivos. Verificar secrets em logs.

---

## 27. MATRIZ DE VALIDAÇÃO

| Área | Existe | Executado | Passou | Falhou | Observação |
| ---- | ------ | --------- | ------ | ------ | ---------- |
| Build | | | | | |
| Unit Tests | | | | | |
| Integration Tests | | | | | |
| Database | | | | | |
| Vault | | | | | |
| API | | | | | |
| Authentication | | | | | |
| Authorization | | | | | |
| Docker | | | | | |
| E2E | | | | | |
| Security | | | | | |

---

## 28–31. PROBLEMAS, PLANO, COMMITS

CRITICAL / HIGH / MEDIUM / LOW / IMPROVEMENT.

Conventional Commits por domínio. **VaultSpring:** sem gitmoji (`docs/guides/writing-style.md`).

---

## 32–36. DOCUMENTAÇÃO, TROUBLESHOOTING, ACEITE, AMBIENTE LOCAL, GATE DOCKER

Problemas documentados devem ser **reais**, não hipotéticos.
Gate mental antes de `docker compose up`.

---

## 37–39. REGRAS SOBRE ALTERAÇÕES, TESTES, VAULT

Priorize funcionamento e segurança. Testes validam comportamento real.

---

## 40. RESULTADO FINAL ESPERADO

Relatório: Executive Summary, Architecture, Baseline, Docker, Vault, API, Auth, Testing, Security, Problems, Corrections, Commits, Documentation, Final Validation.

---

## 41. PRINCÍPIO FINAL

```text
UNDERSTAND → INSPECT → CHECK ENV → CHECK DOCKER → CHECK PORTS → CHECK SERVICES
→ CHECK VAULT → EXECUTE → TEST → VALIDATE → DOCUMENT BASELINE → PLAN
→ IMPLEMENT (somente com autorização) → TEST AGAIN → E2E AGAIN
→ SECURITY VALIDATION → DOCUMENT → SEMANTIC COMMITS → FINAL VALIDATION
```

Primeiro provar o que existe. Depois corrigir. Depois provar novamente.

**VaultSpring delivery:** branch `main` only, PR traceável, commit só quando owner pedir.

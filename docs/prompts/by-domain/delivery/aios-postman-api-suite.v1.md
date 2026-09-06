---
id: prompt.delivery.aios-postman-api-suite
title: AIOS Postman API collections and integration test suite
domain: delivery
purpose: Discovery-first Postman collections for real HTTP/MCP surfaces in ai-operating-system
tags:
  - delivery
  - postman
  - api-testing
  - aios
  - mcp
  - console
  - ci
  - integration-tests
version: 1
status: active
language: pt-BR
ai_ready: true
related_docs:
  - docs/prompts/README.md
  - docs/prompts/pkb-evolution.md
related_prompts:
  - prompt.spring-boot.integral-e2e-vault-docker-audit
  - prompt.delivery.release-readiness
created_at: 2026-09-06
updated_at: 2026-09-06
---

# TASK — Criar suíte profissional de API Collections para o AIOS

> **Catalog note:** Target repository is [ai-operating-system](https://github.com/KleilsonSantos/ai-operating-system) — **not** VaultSpring. Run from the AIOS repo root. VaultSpring PKB stores this as a cross-repo reference asset. **VaultSpring** has its own smoke Collection at [`tests/api/postman/`](../../../tests/api/postman/README.md) (optional, non-blocking). Live Postman runs require **`ok infra`** per [`docs/guides/local-runtime-authorization.md`](../../../guides/local-runtime-authorization.md).

## Contexto

Estou trabalhando no projeto:

https://github.com/KleilsonSantos/ai-operating-system

Projeto: **AI Operating System (AIOS)**

O objetivo desta tarefa é criar uma suíte profissional, organizada e reproduzível de **API Collections e testes de integração** para validar tudo que efetivamente possui uma superfície de interação HTTP/API no projeto até o momento.

A ferramenta principal deve ser **Postman**, salvo se a análise técnica demonstrar de forma objetiva que outra ferramenta é mais adequada.

A implementação deve ser baseada no código real existente no repositório.

---

# 1. REGRA FUNDAMENTAL

NÃO presuma endpoints.

NÃO invente APIs.

NÃO crie requests para funcionalidades que não possuam uma superfície de integração real.

NÃO transforme funções internas TypeScript em endpoints HTTP fictícios.

NÃO assuma que todo engine, package, plugin ou agent possui uma API REST.

NÃO altere a arquitetura do AIOS apenas para facilitar os testes.

Primeiro faça engenharia reversa da superfície de integração existente.

Somente depois crie as Collections.

Toda request criada deve possuir uma origem rastreável no código/documentação do projeto.

---

# 2. OBJETIVO PRINCIPAL

Criar uma suíte de Collections capaz de testar, de maneira organizada:

- health/status;
- endpoints HTTP existentes;
- MCP via HTTP, quando habilitado;
- ações do Control Plane/Console;
- autenticação, caso exista;
- autorização, caso exista;
- workflows;
- validações de entrada;
- respostas de erro;
- contratos;
- integração entre componentes;
- comportamento de providers;
- integrações externas expostas via HTTP;
- cenários positivos;
- cenários negativos;
- edge cases;
- smoke tests;
- regression tests;
- integration tests;
- E2E/API workflows, quando tecnicamente aplicável.

O resultado deve permitir que um desenvolvedor consiga executar:

1. smoke tests;
2. functional tests;
3. negative tests;
4. regression tests;
5. workflow tests;
6. environment-specific tests;
7. CI/API tests;

sem precisar reconstruir manualmente as requests.

---

# 3. PRIMEIRA FASE — AUDITORIA DO PROJETO

Antes de criar qualquer arquivo:

## 3.1 Inspecionar o repositório

Analise:

- `README.md`
- `package.json`
- `pnpm-workspace.yaml`
- `turbo.json`
- `apps/**`
- `packages/**`
- `engines/**`
- `docs/**`
- ADRs relevantes
- configuração do MCP
- configuração do Console
- configuração de providers
- scripts existentes
- testes existentes
- fixtures
- exemplos
- workflows GitHub Actions
- `.env.example`
- configurações de desenvolvimento
- documentação de integração
- qualquer contrato HTTP existente.

Não altere código nesta etapa.

---

# 4. DESCOBRIR A SUPERFÍCIE HTTP REAL

Mapeie explicitamente:

| Componente | Transporte | Endpoint | Método | Origem | Estado |
|---|---|---|---|---|---|
| Console | HTTP | `/api/status` | GET | código/documentação | existente |
| Console | HTTP | `/api/action` | POST | código/documentação | existente |
| MCP | Streamable HTTP | descobrir | descobrir | código/ADR | verificar |
| Outros | HTTP | descobrir | descobrir | código | verificar |

Para cada endpoint encontrado, identificar:

- método;
- path;
- host;
- porta;
- headers;
- content type;
- autenticação;
- request body;
- query parameters;
- path parameters;
- response;
- status codes;
- erros;
- side effects;
- dependências;
- feature flags;
- configuração necessária;
- ambiente necessário.

Se houver dúvida sobre determinado endpoint, investigar o código antes de adicioná-lo.

---

# 5. MCP

O projeto possui suporte a MCP e documentação relacionada a:

- `@aios/mcp`;
- stdio;
- Streamable HTTP;
- `aios_*` tools.

A documentação do projeto indica que:

- stdio é o transporte padrão;
- Streamable HTTP é opt-in.

Portanto:

## NÃO criar testes HTTP para MCP caso o transporte HTTP não esteja habilitado.

Em vez disso:

1. identificar a configuração necessária;
2. identificar o endpoint real;
3. identificar o protocolo;
4. identificar o formato real das mensagens;
5. documentar como habilitar;
6. somente então criar a Collection correspondente.

Separar claramente:

```text
MCP stdio
MCP Streamable HTTP
REST/HTTP API
```

Não misturar os três conceitos.

---

# 6. ESCOLHA DA FERRAMENTA

Use **Postman como ferramenta principal**.

Motivos:

- Collections;
- folders;
- environments;
- variables;
- scripts;
- request chaining;
- Collection Runner;
- CLI;
- CI/CD;
- documentação;
- execução ordenada;
- testes funcionais;
- workflows.

Consultar documentação oficial do Postman antes de implementar detalhes específicos.

Referências principais:

- Postman Collections
- Postman Environments
- Postman Variables
- Postman Scripts
- Collection Runner
- Postman CLI

Não copiar exemplos cegamente.

Adaptar à arquitetura real do AIOS.

---

# 7. ESTRUTURA DE DIRETÓRIOS

Avalie primeiro a estrutura atual do projeto.

Se não existir uma estrutura adequada, criar:

```text
tests/
└── api/
    └── postman/
        ├── collections/
        ├── environments/
        ├── data/
        ├── scripts/
        ├── schemas/
        └── README.md
```

PORÉM:

Não crie essa estrutura automaticamente.

Primeiro verifique se ela se encaixa na arquitetura atual do AIOS.

Se existir uma estrutura melhor no projeto, preserve-a.

A regra é:

> adaptar a estrutura ao projeto, e não adaptar o projeto à Collection.

---

# 8. COLLECTIONS

Organize as Collections por finalidade.

## 8.1 Smoke

Objetivo: verificar rapidamente se o sistema está vivo.

Exemplos possíveis: `GET /api/status` e outros endpoints realmente existentes.

Critérios: serviço disponível; HTTP status esperado; response válido; tempo aceitável; estrutura mínima.

## 8.2 Functional / Negative / Regression / Workflows

- **Functional:** pastas somente para funcionalidades com interface testável (Status, Actions, Pipeline, Providers, Agents, Governance, Policies, MCP).
- **Negative:** body ausente/inválido, JSON inválido, campos obrigatórios, enums, action inexistente, método incorreto, content-type incorreto — descobrir status codes reais no código.
- **Regression:** contratos públicos, endpoints estáveis, pipeline, MCP, control plane, ADRs, bugs documentados — relacionar Test → endpoint → componente → issue/ADR.
- **Workflows:** encadeamento somente se fluxo real existir; variáveis `{{id}}` entre requests.

---

# 9–15. VARIABLES, ENVIRONMENTS, POSTMAN STRATEGY

- Collection vs environment vs local/runtime variables; hierarquia Postman (collection → environment → data → local).
- Ambientes: Local (`base_url` — verificar porta no código), Test/Staging/Production só se existirem; placeholders em prod; nunca commitar secrets.

---

# 16. AUTHENTICATION

Descobrir autenticação HTTP real. Se não existir, não inventar. Se existir, centralizar (ex.: `Bearer {{access_token}}`).

---

# 17–21. TEST SCRIPTS, CONTRACTS, SCHEMAS, DATA, IDEMPOTENCY

- Assertions por request relevante — status codes **reais**, content-type, body, tipos, invariantes.
- Validar contra OpenAPI/schema quando existir; documentar ausência de contrato formal.
- Datasets determinísticos; setup → execute → assert → cleanup quando aplicável.

---

# 22–23. PERFORMANCE E CI/CD

Separar functional vs performance. CI: push → install → build → start AIOS → health → Postman CLI → report — só após verificar workflows/scripts existentes.

---

# 24–28. LOCAL EXEC, INSOMNIA, NOMENCLATURA, ORDEM, TEST IDs

- Comandos reais em `package.json`/README; Postman canônico; Insomnia opcional se import verificado.
- IDs: SMOKE-001, FUNC-001, NEG-001, REG-001, E2E-001 com matriz de rastreabilidade.

---

# 29–33. TRACEABILITY, COVERAGE, SECURITY, PRODUCTION, TEST MATRIX

Matriz Feature → Implementation → HTTP/MCP surface → Collection → Request → Test.

Cobertura: HTTP surface + functional + negative + workflow + error-path — `N/A` quando sem superfície HTTP.

Production protegida; testes destrutivos marcados e restritos a local/test/staging.

---

# 34. DOCUMENTAÇÃO

Criar `tests/api/postman/README.md` (propósito, arquitetura, execução, CLI, CI, segurança, troubleshooting).

---

# 35. FONTES DE VERDADE

```text
1. código do AIOS
2. testes existentes
3. ADRs
4. documentação oficial do projeto
5. package scripts
6. documentação oficial das ferramentas
7. fontes técnicas confiáveis
```

---

# 36. PESQUISA EXTERNA

Consultar documentação oficial Postman (Collections, Environments, Variables, Scripts, Runner, CLI), API testing best practices, MCP Streamable HTTP quando aplicável.

---

# 37. NÃO MODIFICAR O CÓDIGO DA APLICAÇÃO

Artefatos de teste/documentação/CI apenas. Endpoint não testável → documentar e propor melhoria separada.

---

# 38. QUALITY GATE

Repository audit, Collections (smoke/functional/negative/regression/workflows/MCP HTTP se aplicável), environments, assertions, README, traceability matrix, zero secrets commitados.

---

# 39. EXECUTION STRATEGY

| Fase | Entrega |
| ---- | ------- |
| **1 Discovery** | HTTP Surface Map, MCP Surface Map, Environment Map, Existing Test Map, Recommended Architecture — **sem arquivos** |
| **2 Design** | structure, collections, environments, test IDs, CI strategy |
| **3 Implementation** | artefatos Postman + docs |
| **4 Validation** | build, startup, smoke/functional/negative/regression/workflow — classificar PASS/FAIL/BLOCKED/N/A |

---

# 40. FAILURE CLASSIFICATION

TEST_FAILURE · APPLICATION_FAILURE · CONFIGURATION_FAILURE · ENVIRONMENT_FAILURE · DATA_FAILURE · NETWORK_FAILURE · TEST_IMPLEMENTATION_FAILURE · DOCUMENTATION_MISMATCH · UNKNOWN

---

# 41. FINAL REPORT

Implemented · API Surface · Collections · Environments · Coverage · Test Results · Known Limitations · Recommendations

---

# 42–43. ANTI-ALUCINAÇÃO · ANTI-REDUNDÂNCIA

Use `UNKNOWN` / `REQUIRES VERIFICATION` quando não comprovável. Sem collections/environments/tests duplicados.

---

# 44. RESULTADO ESPERADO (referência)

```text
AIOS
├── AIOS - Smoke Tests
├── AIOS - Functional Tests
├── AIOS - Negative Tests
├── AIOS - Regression Tests
├── AIOS - Workflows
└── AIOS - MCP HTTP
```

A estrutura final deve refletir **exatamente as capacidades reais encontradas no AIOS**.

---

# 45. PRINCÍPIO FINAL

Priorize **accuracy, traceability, maintainability, security, reproducibility, automation, coverage, clarity** sobre quantidade artificial de requests.

Primeiro descubra → modele → implemente → execute → valide.

Somente considere concluído quando os artefatos estiverem coerentes com o código real do AIOS.

## AIOS-specific adjustments (owner-approved)

- AIOS is a governance platform (CLI, MCP, Console, pipeline, engines, plugins) — **not** a generic REST API.
- Console HTTP at `127.0.0.1:8787`: `GET /api/status`, `POST /api/action` (verify port in code before use).
- MCP Streamable HTTP is **opt-in**; default transport is stdio — do not conflate.
- Postman Collections + Environments + scripts + Collection Runner/CLI for CI when justified.

## VaultSpring delivery (when catalog lives here)

- Run prompt in **AIOS repo root**.
- PKB catalog only until owner says `ok` / `prossegue` to execute phases 2–4.
- Infra gate for live Console/MCP HTTP: **`ok infra`**.

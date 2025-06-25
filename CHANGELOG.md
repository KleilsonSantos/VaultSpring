# 📦 Changelog

Todas as alterações importantes neste projeto serão documentadas aqui.


## [0.1.2-SNAPSHOT] - 2025-06-24

### ✨ Documentação

- ✨ Melhorias significativas no `README.md`, com:
  - Descrição mais clara dos objetivos do projeto.
  - Destaque das tecnologias utilizadas.
  - Orientações aprimoradas para primeiros passos e execução local.

- 📘 Atualização do `HELP.md`, incluindo:
  - Instruções detalhadas para desenvolvedores.
  - Execução local com Docker e PostgreSQL.
  - Uso de scripts auxiliares (`wait-for-db.sh`, `act-dev.sh`).
  - Orientações sobre CI, GITHUB_TOKEN, e configuração de ambiente.

### 🔖 Versão
- Atualizado `pom.xml` para `0.1.2-SNAPSHOT`.

## [0.1.1-SNAPSHOT] - 2025-06-25

### 🔧 Chore
- Atualiza `act-dev.sh` para incluir suporte ao `GITHUB_TOKEN`
- Adiciona script `wait-for-db.sh` para aguardar readiness do PostgreSQL antes da inicialização da aplicação
- Atualiza scripts do GitHub Actions para refletir as mudanças de ambiente

### 🔖 Versão
- Atualizado `pom.xml` para `0.1.1-SNAPSHOT`.

## [0.1.0] - 2025-06-24

### ✨ Added
- Arquivos de configuração separados para produção (`application-prod.yml`) e desenvolvimento (`application-dev.yml`)
- Novo endpoint de criação de usuário no `UserController`
- Integração do Flyway para versionamento de banco de dados com perfis Maven separados (`flyway-dev` e `flyway-prod`)

## 🔖 Versão
- Atualizado `pom.xml` para `0.1.0`.

### 🔧 Changed
- `Dockerfile` atualizado para aceitar perfis via variável de ambiente `SPRING_PROFILES_ACTIVE` e utilizar build multi-stage
- Atualização do `pom.xml` com perfis Maven dedicados ao Flyway para ambientes distintos (`dev` e `prod`)

## [0.0.15] - 2025-06-22

### ♻️ Changed
- Atualização do `.dockerignore` para evitar arquivos sensíveis no build
- Ajuste no `.render.yaml` para usar corretamente o `app.jar` como ponto de entrada
- Otimização do `Dockerfile` com multistage build para produção

## 🔖 Versão
- Atualizado `pom.xml` para `0.0.15`.
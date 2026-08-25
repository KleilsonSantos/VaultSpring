<div align="center">

<p align="center">
  <img src="https://miro.medium.com/v2/resize:fit:720/format:webp/1*XvXF_f1HdUPshLKVHbFrXw.gif" alt="VaultSpring Animation" width="600"/>

<h3 align="center">
  <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&weight=600&size=22&pause=1500&color=2ECC71
&center=true&vCenter=true&width=600&lines=Gerenciamento+Seguro+de+Segredos;Para+Aplicacoes+Java;com+Spring+Boot+|+Docker+|+Vault" alt="Animation 2" />
</h3>

#### <strong>🔐 VaultSpring - Segurança, Observabilidade e Arquitetura Moderna ☕️</strong><br>
#### ⚙️ Solução segura para gerenciamento de segredos com integração a Vault, CI/CD, testes e práticas DevSecOps.<br>
#### ✅ Ideal para aplicações Java modernas com foco em segurança, escalabilidade e automação.

</div>

<div align="center">
  <!-- Versão de tecnologia -->
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.16-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
  <img src="https://img.shields.io/badge/Vault-Compose-175DDD?style=for-the-badge&logo=vault&logoColor=white" alt="Vault"/>
  <img src="https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Lombok-E9573F?style=for-the-badge&logo=lombok&logoColor=white" alt="Lombok"/>

  <!-- Observabilidade e testes -->
  <img src="https://img.shields.io/badge/Micrometer-007ACC?style=for-the-badge&logo=prometheus&logoColor=white" alt="Micrometer"/>
  <img src="https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white" alt="Prometheus"/>
  <img src="https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit5"/>
  <img src="https://img.shields.io/badge/Mockito-8A2BE2?style=for-the-badge&logo=mockito&logoColor=white" alt="Mockito"/>

  <!-- Git e automações -->
  <img src="https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white" alt="GitHub Actions"/>
  <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white" alt="GitHub"/>
  <img src="https://img.shields.io/badge/Git-F05033?style=for-the-badge&logo=git&logoColor=white" alt="Git"/>

  <!-- Licença, status e contribuição -->
  <img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=for-the-badge" alt="Apache License"/>
  <img src="https://img.shields.io/badge/Status-Em%20Desenvolvimento-orange.svg?style=for-the-badge" alt="Status"/>
  <img src="https://img.shields.io/badge/Contribuições-Bem%20vindas-brightgreen.svg?style=for-the-badge" alt="Contributions Welcome"/>
</div>

<br>

<div align="center">
<!-- Status dinâmico de build e cobertura -->
  <img src="https://github.com/KleilsonSantos/VaultSpring/actions/workflows/maven.yml/badge.svg?branch=main&style=for-the-badge" alt="Build Status"/>
  <img src="https://codecov.io/gh/KleilsonSantos/VaultSpring/branch/main/graph/badge.svg?style=for-the-badge" alt="Cobertura de Testes"/>
</div>

<div align="center">

>#### ⚠️ **Este projeto está 🚀 _(Em Constante Desenvolvimento 🚧)_**
> Algumas funcionalidades podem estar incompletas ou sujeitas a alterações. Contribuições são bem-vindas! 🛠️

</div>

## 📝 Descrição

**Gerenciamento Seguro de Segredos para Aplicações Java com Spring Boot e Docker Vault**

## 👀 Visão geral do projeto

VaultSpring é uma aplicação **Spring Boot** desenvolvida para proporcionar **gestão segura de segredos** em ambientes
modernos, escaláveis e de alta confiabilidade. O projeto adota uma arquitetura modular e robusta, garantindo fácil
manutenção, extensibilidade e segurança em todas as camadas.

### 🏗️ **Arquitetura e organização (código atual)**

Pacotes em `src/main/java/com/vaultspring`:

- **controller** — HTTP API (`/api/v1/users`)
- **dto** — `UserRequest` / `UserResponse` (a senha nunca sai na resposta)
- **service** — regras de aplicação e hash BCrypt
- **entity** / **repository** — JPA + Spring Data
- **config** — `SecurityConfig`, `OpenApiConfig`
- **exception** — RFC 7807 `ProblemDetail`

Configuração em `src/main/resources/` (`application.yml`, perfis `dev` / `prod` / `hom` / `vault`, Flyway em `db/migration/`).

Vault roda no **Docker Compose** (`vault/config/vault.hcl`). O app usa **`spring-cloud-starter-vault-config`** (Spring Cloud **2025.0.x**) com perfil `vault` / `prod-vault` e KV v2 em `secret/vaultspring`. Render/prod sem Vault continua com `SPRING_DATASOURCE_*`.

### 🔗 **O que está integrado**

- PostgreSQL + Flyway (perfil `prod` / `hom` / `dev`)
- Spring Cloud Vault (KV v2) — perfil `vault`; desligado em `test`
- Actuator: `/actuator/health`, `/actuator/info`, `/actuator/prometheus`
- OpenAPI / Swagger UI (dev)
- Spring Security filter chain (`SecurityConfig`) — JWT tracked in #6
- RFC 7807 errors, OpenAPI (springdoc 2.9.0), Testcontainers IT
- CI: Checkstyle, unit verify, integration-tests, dependency-review, docker-build, JaCoCo, Codecov, CodeQL v4, SonarCloud
- Governança AIOS: [`docs/`](./docs/README.md), issues, SemVer — ver [`CONTRIBUTING.md`](./CONTRIBUTING.md)
- Agentes de IA: `AGENTS.md`, `.cursor/rules/`, `.github/agents/`

### 🚧 **Próximo (não inventado como pronto)**

- JWT authentication — issue #6 (filter chain baseline in #32)
- Migração planejada para **Spring Boot 4.x** — issue #33

Contribuição e fluxo Git: [`CONTRIBUTING.md`](./CONTRIBUTING.md) · segurança: [`SECURITY.md`](./SECURITY.md)

## 🔥 Tecnologias utilizadas

- **Java 17**
- **Spring Boot 3.5.16** + **Spring Cloud 2025.0.x** (Vault Config)
- **Docker Compose** (PostgreSQL 15, Vault, SonarQube LTS Community, pgAdmin)
- **PostgreSQL** + **Flyway** (`flyway-core` + `flyway-database-postgresql`)
- **Lombok**
- **Actuator** + **Micrometer Prometheus**
- **Caffeine**
- **OWASP Dependency-Check** (perfil Maven)
- **Maven Wrapper**
- **GitHub Actions** + **Dependabot** + **CodeQL**

## 📘 Documentação

| Doc | Conteúdo |
| --- | -------- |
| [`docs/README.md`](./docs/README.md) | Índice técnico (C4, fluxos Mermaid, ADRs) |
| [`HELP.md`](./HELP.md) | Início rápido |
| [`CONTRIBUTING.md`](./CONTRIBUTING.md) | Contribuição e quality gates |
| [`CHANGELOG.md`](./CHANGELOG.md) | Histórico de versões |
| [`SECURITY.md`](./SECURITY.md) | Segurança e reporte de vulnerabilidades |

## 📘 Guia Rápido

Para execução local, testes e troubleshooting: [`HELP.md`](./HELP.md) e [`docs/development.md`](./docs/development.md).

## ✅ **O que já foi concluído**

- ✅ Estrutura modular (controller, dto, service, entity, repository)
- ✅ Lombok; DTOs sem MapStruct (conversão no `UserService`)
- ✅ RFC 7807 `ProblemDetail`, OpenAPI (springdoc 2.9.0), `SecurityFilterChain`
- ✅ Testcontainers IT (`UserApiIT`) + job CI `integration-tests`
- ✅ Documentação técnica (`docs/`: architecture, configuration, development, api)
- ✅ PostgreSQL + Flyway (prod/hom/dev); H2 no perfil `test`
- ✅ Perfis `dev`, `prod`, `hom`
- ✅ Endpoint de usuários com hash BCrypt e resposta sem senha
- ✅ Dockerfile multi-stage (`target/app.jar`)
- ✅ GitHub Actions: Checkstyle, JaCoCo, integration-tests, dependency-review, CodeQL v4
- ✅ Docker Compose (Postgres, Vault local, SonarQube LTS Community)
- ✅ Spring Cloud Vault (KV v2, perfil `vault` / `prod-vault`)

> ⚡ Essas entregas garantem uma base sólida para o gerenciamento seguro de segredos em aplicações Java modernas.

## 🌍 **Junte-se a nós**

Se você acredita que este projeto pode ajudar outros desenvolvedores, compartilhe com sua rede! Vamos construir juntos
uma infraestrutura de desenvolvimento mais eficiente e acessível para todos. 🚀✨

## 🛠️ **Contribua e faça parte da comunidade**

Este projeto é **open-source** e está em constante evolução. Sua contribuição é muito bem-vinda! Seja você um
desenvolvedor experiente ou iniciante, há várias formas de ajudar:

- 💬 **Sugira melhorias:** Abra uma issue com suas ideias.
- 🛠️ **Contribua com código:** Envie pull requests com novas funcionalidades ou correções.
- ⭐ **Dê uma estrela no GitHub:** Isso ajuda o projeto a alcançar mais desenvolvedores.

## 📄 Licença

Este projeto está sob a licença Apache 2.0. Veja mais detalhes

> 💡 **Nota:** Este projeto está em constante desenvolvimento. Algumas funcionalidades podem estar incompletas ou
> sujeitas a alterações. Contribuições são sempre bem-vindas! 🛠️

## 🙏 **Agradecimentos**

Agradecemos por utilizar este projeto! Caso tenha sugestões, melhorias ou encontre algum problema, sinta-se à vontade
para abrir uma issue ou enviar um pull request. Sua contribuição é muito bem-vinda! 💡

## ✍️ **Autor**

Desenvolvido por **Kleilson Santos**.

## 📬 Contato

- 📧 [Email](kleilson@icloud.com) kleilson@icloud.com
- 🌐 [GitHub](https://github.com/KleilsonSantos) - KleilsonSantos
- 💼 [LinkedIn](https://www.linkedin.com/in/kleilson-dev-full-stack) - KleilsonSantos

<p align="center">⚡ Construa com segurança, teste com propósito, automatize com clareza.<br>Kleilson Santos 🚀</p>
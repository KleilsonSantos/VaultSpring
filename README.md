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
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
  <img src="https://img.shields.io/badge/Vault-175DDD?style=for-the-badge&logo=vault&logoColor=white" alt="Vault"/>
  <img src="https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/MapStruct-FF6F00?style=for-the-badge&logo=java&logoColor=white" alt="MapStruct"/>
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

### 🏗️ **Arquitetura e Organização**

O projeto está estruturado em múltiplos módulos e pacotes, cada um com responsabilidades bem definidas:

- **config**: Centraliza toda a configuração da aplicação, incluindo integração com Vault, PostgreSQL, segurança, beans
  customizados e propriedades externas. Isso garante flexibilidade e desacoplamento das dependências.
- **constantes**: Armazena valores fixos e chaves reutilizáveis, promovendo padronização e evitando duplicidade de
  informações sensíveis ou recorrentes.
- **dto**: Define os objetos de transferência de dados (Data Transfer Objects), separando claramente as entidades de
  domínio das estruturas expostas via API, facilitando a validação e a evolução da interface.
- **enums**: Centraliza os tipos enumerados para mensagens, status, erros e campos, promovendo consistência e reduzindo
  erros de digitação em toda a aplicação.
- **mapper**: Utiliza MapStruct para conversão eficiente e segura entre entidades e DTOs, garantindo que os dados
  trafeguem corretamente entre as camadas de persistência e apresentação.
- **response**: Estrutura as respostas padronizadas da API, encapsulando dados, mensagens e metadados, o que facilita o
  consumo por clientes e integrações externas.
- **util**: Reúne utilitários e helpers reutilizáveis, como builders de resposta, facilitando a manutenção e a
  padronização de comportamentos comuns.

### 🔗 **Integração Funcional**

- **Integração com Vault**: Gerenciamento seguro de segredos, autenticação e autorização centralizadas.
- **Integração com PostgreSQL**: Persistência robusta e escalável dos dados de usuários e segredos.
- **DTOs e Mappers**: Separação clara entre domínio e API, com conversão automática e validação de dados.
- **Respostas Padronizadas**: Todas as respostas seguem um padrão consistente, facilitando o tratamento de erros e
  sucesso no frontend e em integrações.
- **Enums e Constantes**: Redução de erros e aumento da legibilidade do código.
- **Configurações Centralizadas**: Facilidade para ajustes de ambiente, segurança e integrações externas.

### 🚀 **Diferenciais da Arquitetura**

- **Modularidade**: Cada responsabilidade está isolada, facilitando testes, manutenção e evolução.
- **Segurança**: Práticas avançadas de segurança desde a configuração até o tratamento de dados sensíveis.
- **Extensibilidade**: Estrutura pronta para integração com novos serviços, bancos de dados e provedores de
  autenticação.
- **Padronização**: Uso intensivo de DTOs, mappers, enums e respostas customizadas, promovendo qualidade e
  previsibilidade.

> 💡 **Resumo:** O VaultSpring já conta com uma base arquitetural sólida, cobrindo desde a configuração centralizada até
> a padronização de respostas e integração segura com serviços críticos, tornando-se uma solução moderna e confiável
> para
> o gerenciamento de segredos em aplicações Java.

## 🔥 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.4.5**
- **Spring Cloud 2024.0.1**
- **Spring Cloud Vault**
- **Docker & Docker Vault**
- **PostgreSQL**
- **MapStruct**
- **Lombok**
- **Micrometer & Prometheus**
- **Caffeine (Cache)**
- **OWASP Dependency-Check**
- **Maven**

## 📘 Guia Rápido e Avançado

Para instruções detalhadas de uso local, testes, cobertura, e troubleshooting:  
➡️ Acesse o arquivo [`HELP.md`](./HELP.md)

## ✅ **O que já foi concluído**

- ✅ Estrutura modular do projeto para fácil manutenção e extensibilidade
- ✅ Aplicação de boas práticas com Lombok e MapStruct
- ✅ Documentação inicial (`README.md` e `HELP.md`) e instruções de execução local
- ✅ Integração completa com banco de dados PostgreSQL
- ✅ Separação de arquivos de configuração para produção e desenvolvimento (`application-prod.yml` e `application-dev.yml`)
- ✅ Integração do Flyway para versionamento de banco de dados com perfis Maven dedicados (`flyway-dev` e `flyway-prod`)
- ✅ Novo endpoint de criação de usuário no `UserController`
- ✅ Dockerfile otimizado com suporte a múltiplos estágios e variáveis de ambiente para perfis
- ✅ Pipeline CI/CD com GitHub Actions: build, testes automatizados, análise com SonarCloud, Checkstyle e cobertura com JaCoCo
- ✅ Suporte a execução e build via Docker e Docker Compose
- ✅ Cobertura de testes automatizada com JUnit 5, Mockito e JaCoCo
- ✅ Integração com HashiCorp Vault para gerenciamento seguro de segredos

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
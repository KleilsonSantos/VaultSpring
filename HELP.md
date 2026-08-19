# 🛠️🧪 VaultSpring - Manual Técnico de Uso (HELP.md)

## 🚀 Funcionalidades
- ✅ Execução local 
- ✅ Cobertura de testes
- ✅ Integração com Docker Compose (PostgreSQL, Vault local, SonarQube)
- ✅ Integrações CI (Checkstyle, JaCoCo, CodeQL, Sonar em `main`)
- ✅ Vault como serviço Compose — o app Spring **ainda não** usa `spring-cloud-vault`
- ✅ Integração com banco de dados PostgreSQL
- ✅ Arquitetura modular e extensível
- ✅ Estrutura modular do projeto para fácil manutenção
- ✅ Aplicação de boas práticas com Lombok
- ✅ Documentação inicial e instruções de execução

### 🌟 Para visão geral do projeto e tecnologias utilizadas, veja o [`README.md`](./README.md)

## 🚀 Início Rápido

### Pré-requisitos
- ☕ Java 17+
- 🐘 Maven 3.8+
- 🐳 Docker e Docker Compose
- 🛢️ PostgreSQL (rodando via container ou local)

## 🧩 Como Executar Localmente

### 🔧 Clone o projeto

```bash
git clone https://github.com/KleilsonSantos/vaultspring.git
```
```
cd vaultspring
```

## 🔧 Ajuste as variáveis de ambiente:
```
export POSTGRES_DB=seu_db
export POSTGRES_USER=seu_user
export POSTGRES_PASSWORD=seu_password
export POSTGRES_DB=seu_db
export SONAR_TOKEN=seu_token
export SONAR_HOST_URL=http://localhost:9000

```
## 🔧 Inicia o banco e a aplicação:
```
docker-compose up -d postgres
./mvnw spring-boot:run
```

### 💡 Dica: Para executar o projeto

```
mvn clean install
```

### Execute o seguinte comando:

```
mvn spring-boot:run
```

## 📌 Acesse a aplicação

- A API estará disponível em: http://localhost:8080
- Utilize ferramentas como Postman ou curl para interagir com os endpoints.

## 📦 Comandos Úteis
### 🔍 Geração de cobertura de testes com Jacoco

```
mvn clean test
```
```
mvn jacoco:report
```
### 📊 Análise com Sonar Scanner::

```
target/site/jacoco/index.html
```

## 🚨 Rodando análise local com Sonar Scanner + ACT

### 🔧 Executanto Sonar Scanner Localmente:
```
make sonar
```

### 🔧 Executando ACT localmente:
```
scripts/act-dev.sh
```
>Certifique-se que o SonarQube esteja rodando com o token correto e permissões no projeto.

## 🧪 Testes

### Este projeto utiliza:

- ✅ JUnit 5
- ✅ Mockito
- ✅ Jacoco para cobertura
- ✅ GitHub Actions para testes automatizados

## 🔧 Execute testes locais com:
```
mvn clean test
```

## 🔐 HashiCorp Vault (infraestrutura local)

O Compose sobe o Vault com [`vault/config/vault.hcl`](./vault/config/vault.hcl) (TLS desligado, **só loopback/dev**).

O processo Spring **não** lê segredos do Vault ainda: não há `spring-cloud-starter-vault-config` no `pom.xml`. Quando essa integração for feita, use o train Spring Cloud **2025.0.x** (Boot 3.5), não o BOM 2024.0.x (Boot 3.4).

Credenciais de banco no perfil `prod` vêm de `SPRING_DATASOURCE_*` / `POSTGRES_*`.

## 🔄 CI/CD com GitHub Actions

Pipeline em [`.github/workflows/maven.yml`](./.github/workflows/maven.yml):

- Checkstyle + `./mvnw verify` (JaCoCo)
- Codecov (não bloqueia o build)
- CodeQL v4 (`java-kotlin`, build Maven manual)
- SonarQube Cloud no **push para `main`** — secrets `SONAR_TOKEN` e variável `SONAR_ORGANIZATION`

Contribuição: [`CONTRIBUTING.md`](./CONTRIBUTING.md). Agentes de IA: [`AGENTS.md`](./AGENTS.md).
## 🐋 Docker

### Embora o foco esteja no ambiente local com Maven, o projeto suporta construção com Docker.
Criando imagem
```
./mvnw spring-boot:build-image -DskipTests
```

### Rodando imagem
```
docker run -p 8080:8080 vaultspring:latest
```
## 📚 Referências
### Spring & Maven

- Spring Boot Docs
- Maven Plugin Guide
- Spring Cloud Vault

## 📚 Exemplos e guias

- [Building REST APIs](https://spring.io/guides/gs/rest-service/) - Building REST APIs
- [Spring Boot Maven Plugin Reference Guide](https://spring.io/guides/gs/serving-web-content/) - Spring MVC Web Content

## 🧰 Maven Inheritance (herança indesejada)

Este projeto sobrescreve elementos indesejados herdados do parent do Spring Boot (como <license> e <developers>).
Caso troque o parent, remova os overrides manualmente.

### 📢 Dica final: Se você encontrar problemas de conexão com o banco, variáveis de ambiente ausentes ou falhas no Vault, consulte a aba Issues ou abra uma nova com detalhes para contribuir com a evolução do projeto.
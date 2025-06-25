# 🛠️🧪 VaultSpring - Manual Técnico de Uso (HELP.md)

## 🚀 Funcionalidades
- ✅ Execução local 
- ✅ Cobertura de testes
- ✅ Integração com Docker
- ✅ Integrações CI
- ✅ Integração com Vault
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

### 🔧 Executanto ACT Localmente:
```
script/act-dev.sh
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

## 🔐 Integração com HashiCorp Vault

### A aplicação está configurada para se conectar ao Vault em runtime. Certifique-se que seu Vault:

>Está rodando (local ou remoto)

>Possui os segredos esperados em /secret/application ou conforme seu bootstrap.yml

>Está acessível no container da aplicação

## 🔧 Exemplo de configuração:

```yaml
spring:
  cloud:
    vault:
      uri: http://localhost:8200
      authentication: TOKEN
      token: ${VAULT_TOKEN}
    kv:
     enabled: true
     backend: secret
```

## 🔄 CI/CD com GitHub Actions

### O projeto possui um pipeline automatizado configurado em .github/workflows/maven.yml com as seguintes etapas::

- ✅ Lint e validação com Maven
- ✅ Testes automatizados
- ✅ Análise com SonarCloud
- Checkout do código
- Configuração do JDK 17 (usando Temurin)
- Instalação do Maven (para execuções locais com ACT)
- Cache do repositório Maven para builds mais rápidos
- Análise de código com Checkstyle
- Build, execução de testes e geração de relatório de cobertura (JaCoCo)
- Upload do relatório de cobertura como artefato
- Envio do grafo de dependências
- Análise de qualidade com SonarQube/SonarCloud (usando secrets SONAR_TOKEN e SONAR_HOST_URL)
- Upload do JAR gerado como artefato
- Envio do relatório de cobertura para o Codecov

### Secrets usados no pipeline (.github/workflows/maven.yml):

```
env:
 SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
 SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}
 GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```
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
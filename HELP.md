# 🛠️ VaultSpring — Manual técnico (HELP.md)

Guia rápido. Documentação completa: **[`docs/README.md`](./docs/README.md)**.

## Visão geral

- Spring Boot **3.5.16**, Java **17**, PostgreSQL **15**, Flyway  
- **Spring Cloud Vault Config** (KV v2) — perfis `vault` / `prod-vault`  
- **Spring Security** (`SecurityFilterChain`) — JWT em [#6](https://github.com/KleilsonSantos/VaultSpring/issues/6)  
- OpenAPI / Swagger UI no perfil **`dev`**  

## Início rápido

```bash
git clone https://github.com/KleilsonSantos/VaultSpring.git
cd VaultSpring
cp .env.example .env

docker compose up -d postgres
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

- API: http://localhost:8080/api/v1/users  
- Swagger: http://localhost:8080/swagger-ui.html  
- Health: http://localhost:8080/actuator/health  

Passo a passo detalhado: [`docs/development.md`](./docs/development.md).

## Vault (Compose)

1. `docker compose up -d postgres vault`  
2. Init/unseal Vault; definir `VAULT_TOKEN` no `.env`  
3. `bash scripts/vault-seed-dev.sh`  
4. `docker compose up -d app` (perfil `prod-vault`)  

Render/prod sem Vault: `SPRING_PROFILES_ACTIVE=prod` + `SPRING_DATASOURCE_*`.

## Testes

```bash
./mvnw -B checkstyle:check test              # unitários (H2)
./mvnw -B verify -Pintegration-tests        # Testcontainers (Docker)
make test-all                               # equivalente via Makefile
```

Relatório JaCoCo: `target/site/jacoco/index.html` após `./mvnw verify`.

## CI/CD

Pipeline [`.github/workflows/maven.yml`](./.github/workflows/maven.yml):

| Job | Quando |
| --- | ------ |
| `quality` | Checkstyle + unit verify + JaCoCo + Codecov |
| `integration-tests` | Failsafe + Testcontainers |
| `dependency-review` | PRs — severidade high bloqueia |
| `docker-build` | Smoke `docker build` |
| `codeql` | CodeQL Action v4 (`java-kotlin`) |

SonarQube Cloud: **Automatic Analysis** (check `SonarCloud Code Analysis` no PR).  
Release: tag anotada `v*.*.*` → [`.github/workflows/release.yml`](./.github/workflows/release.yml).

Contribuição: [`CONTRIBUTING.md`](./CONTRIBUTING.md).

## Docker (imagem local)

```bash
./mvnw -B package -DskipTests
docker build -t vaultspring:local .
```

## Referências internas

| Doc | Conteúdo |
| --- | -------- |
| [`docs/architecture.md`](./docs/architecture.md) | Camadas, segurança, Vault |
| [`docs/configuration.md`](./docs/configuration.md) | Perfis e variáveis de ambiente |
| [`docs/api.md`](./docs/api.md) | Endpoints e erros RFC 7807 |
| [`docs/guides/`](./docs/guides/) | Git, kickoff, releases |

Problemas: abra uma [issue](https://github.com/KleilsonSantos/VaultSpring/issues) com perfil, logs e passos (sem segredos).

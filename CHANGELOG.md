# 📦 Changelog

Todas as alterações importantes neste projeto serão documentadas aqui.

---

## [0.1.0] - 2025-06-24

### ✨ Added
- Arquivos de configuração separados para produção (`application-prod.yml`) e desenvolvimento (`application-dev.yml`)
- Novo endpoint de criação de usuário no `UserController`
- Integração do Flyway para versionamento de banco de dados com perfis Maven separados (`flyway-dev` e `flyway-prod`)

### 🔧 Changed
- `Dockerfile` atualizado para aceitar perfis via variável de ambiente `SPRING_PROFILES_ACTIVE` e utilizar build multi-stage
- Atualização do `pom.xml` com perfis Maven dedicados ao Flyway para ambientes distintos (`dev` e `prod`)

---

## [0.0.15] - 2025-06-22

### ♻️ Changed
- Atualização do `.dockerignore` para evitar arquivos sensíveis no build
- Ajuste no `.render.yaml` para usar corretamente o `app.jar` como ponto de entrada
- Otimização do `Dockerfile` com multistage build para produção

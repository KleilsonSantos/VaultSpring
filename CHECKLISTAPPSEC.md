# ✅ Checklist de Testes de Segurança - VaultSpring

> Testes comuns contra vulnerabilidades como SQLi, XSS, CSRF, DDoS e validações de configurações seguras.

---

## 📊 Ambiente Seguro

- [X] Configuração da `SecurityFilterChain` (`SecurityConfig` — health público, Prometheus autenticado)
- [X] Regras de CORS aplicadas em `/api/**`
- [ ] Headers sensíveis removidos (X-Powered-By, Server)
- [X] Configuração de CSRF segura (ou desabilitado com cautela)
- [ ] Logs para eventos de autenticação e autorização ativados
- [ ] Token JWT com tempo de expiração e revogação

---

## 🔐 Análise Estática (SAST)

- [X] Rodar OWASP Dependency Check
- [X] Rodar SonarQube com perfil de regras OWASP ativado
- [ ] Rodar SpotBugs + FindSecBugs

---

## ⚡ Ataques SQL Injection

- [ ] Enviar `1' or '1'='1` e `UNION SELECT ...` em campos da API via Postman
- [ ] Rodar `sqlmap` para detectar falhas

---

## 🤯 Ataques XSS

- [ ] Testar `<script>alert(1)</script>` em inputs HTML
- [ ] Testar campos refletidos na resposta

---

## 🧳 Ataques CSRF

- [ ] Criar HTML com `form` forjado para testar POST sem token
- [ ] Verificar proteção CSRF do Spring

---

## ✨ Headers e Configuração

- [ ] `Strict-Transport-Security` presente?
- [ ] `Content-Security-Policy` definido?
- [ ] `Referrer-Policy`, `X-Frame-Options`, `X-Content-Type-Options` aplicados?

---

## 🌪️ DDoS / Stress Test

- [ ] Rodar `ab -n 5000 -c 200` contra /health
- [ ] Rodar `slowloris` com requisições lentas

---

## ⚖️ Testes de Timeout / Chaos

- [ ] Testar timeouts e fallback com `Resilience4j`
- [ ] Injetar falhas com `Spring Boot Chaos Monkey`

---

## 🔒 Validação de JWT

- [ ] JWT expira corretamente
- [ ] JWT malformado é rejeitado
- [ ] JWT sem escopo/role é bloqueado

---

## ✅ Testes Automatizados

- [ ] `make security-check`
- [ ] `make sqli-test`
- [ ] `make xss-test`
- [ ] `make ddos-test`
- [ ] `make zap-scan`
- [ ] `make verify-jwt`

---

> Rodar este checklist ao menos 1x por sprint ou antes de PRs importantes.
> Ambiente de teste isolado é altamente recomendado!
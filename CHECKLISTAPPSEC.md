# Security test checklist — VaultSpring

Manual AppSec checklist for local/staging validation. **Not** a substitute for CI (Checkstyle, CodeQL, SonarCloud, dependency-review). Do not commit exploit PoCs or scan output with secrets.

Style: no emoji in section titles — see [`docs/guides/writing-style.md`](docs/guides/writing-style.md).

---

## Secure environment

- [x] `SecurityFilterChain` configured (`SecurityConfig` — health public, Prometheus authenticated)
- [x] CORS on `/api/**`
- [ ] Sensitive headers removed (X-Powered-By, Server)
- [x] CSRF disabled with documented rationale (stateless API baseline)
- [ ] AuthN/AuthZ audit logging enabled
- [x] JWT login at `POST /api/v1/auth/login` (HS256 Bearer — [#6](https://github.com/KleilsonSantos/VaultSpring/issues/6))
- [x] Per-resource authorization / RBAC on `/api/v1/users` (`USER` → `/me`; `ADMIN` → list/create) — [#103](https://github.com/KleilsonSantos/VaultSpring/issues/103)
- [ ] Rate limiting on login and sensitive endpoints

---

## Static analysis (SAST)

- [x] OWASP Dependency-Check (`./mvnw verify -Pdependency-check`)
- [x] SonarCloud on `main` (Automatic Analysis)
- [ ] SpotBugs + FindSecBugs (not in `pom.xml` today)

---

## SQL injection

- [ ] Test `' OR '1'='1` and `UNION SELECT` on API fields
- [ ] Optional: `sqlmap` against isolated staging only

---

## XSS

- [ ] Reflected/stored payloads in inputs (if applicable)

---

## CSRF

- [ ] Forged form POST when CSRF is re-enabled for cookie sessions

---

## Security headers

- [ ] `Strict-Transport-Security` (prod — partial in `SecurityConfig`)
- [ ] `Content-Security-Policy`
- [ ] `Referrer-Policy`, `X-Frame-Options`, `X-Content-Type-Options`

---

## Load / stress

- [ ] Controlled load against `/actuator/health` (staging only)

---

## JWT

- [x] Token expiry configured (`vaultspring.jwt.expiration-seconds`)
- [x] Prod/hom fail-fast when JWT secret missing or template placeholder (`JwtSecretGuard`)
- [ ] Malformed / expired token rejected (covered by unit/IT — verify manually on staging)
- [ ] `iss` / `aud` / `jti` claims and revocation strategy
- [ ] RS256 or asymmetric keys for production (today: HS256 symmetric)

---

## Vault (Database Secrets Engine)

- [x] Dynamic JDBC credentials via `vault` / `prod-vault` profiles ([ADR-0005](docs/adr/0005-dynamic-postgresql-credentials-vault.md))
- [ ] Vault AppRole / Kubernetes auth (today: `TOKEN` in dev Compose)
- [ ] Lease rotation at `max_ttl` validated in staging (`VaultDatabaseCredentialRotation`, metric `vaultspring.vault.database.rotation.total`)

---

## Makefile targets (optional, staging)

Some targets in `Makefile` reference endpoints not yet implemented — verify against `docs/api.md` before running.

- [ ] `make check-sec`
- [ ] `make zap-scan` (staging)

---

Run this checklist before major releases or after security-related changes. Use an isolated environment.

Comprehensive audit prompt: [`docs/prompts/by-domain/security/comprehensive-appsec-audit.v1.md`](docs/prompts/by-domain/security/comprehensive-appsec-audit.v1.md).

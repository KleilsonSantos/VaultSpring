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
- [ ] JWT expiry and revocation (blocked on [#6](https://github.com/KleilsonSantos/VaultSpring/issues/6))

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

## JWT (after #6)

- [ ] Token expiry enforced
- [ ] Malformed token rejected
- [ ] Missing scope/role blocked

---

## Makefile targets (optional, staging)

Some targets in `Makefile` reference endpoints not yet implemented — verify against `docs/api.md` before running.

- [ ] `make check-sec`
- [ ] `make zap-scan` (staging)

---

Run this checklist before major releases or after security-related changes. Use an isolated environment.

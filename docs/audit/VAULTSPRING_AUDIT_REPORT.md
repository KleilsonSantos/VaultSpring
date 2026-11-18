# VaultSpring — End-to-End Engineering & Security Audit

**Date:** 2026-10-09
**Branch / commit audited:** `sandbox` @ `aa496eff208150464e732237c9ec0eb7dd98d9f0` (2026-10-02 22:06:14 -0300)
**Working tree at audit time:** 2 modified files not yet committed (`AGENTS.md`, `docs/README.md` — 1 line each), 1 untracked file (`docs/guides/testing-execution-policy.md`). None of these were altered by this audit; this report is a new, separate artifact.
**Mode:** Read-only static audit + one authorized dynamic check (`./mvnw -B checkstyle:check test`, no live infra). No code was modified, no infra was started, no secrets were exfiltrated, nothing was committed or pushed.
**Scope:** Full repository — architecture, AppSec/threat model, Vault/secrets, backend (Java/Spring), database/Flyway, test strategy, CI/CD, Docker/infra, supply chain, observability, resilience, performance (proportional), documentation, governance/traceability, licensing.

---

## 1. Executive summary

VaultSpring is a compact, well-engineered Spring Boot 3.5.16 / Java 17 portfolio project (46 main classes, 19 test classes, 2 CI workflows, 4 Flyway migrations) that demonstrates real secure-backend-engineering practice: a deny-by-default Spring Security chain, RFC 7807 problem details, JWT with a fail-fast secret guard, Vault-issued dynamic PostgreSQL credentials with graceful rotation, structured logging with a dedicated log-sanitizer, correlation-ID propagation, and a CI pipeline with SAST (CodeQL), dependency-review, and SHA-pinned GitHub Actions. The build is green: **`checkstyle:check` passes with zero violations and all 67 unit tests pass** (`BUILD SUCCESS`, 3m27s).

That said, the audit found **one HIGH/likely-CRITICAL finding that needs immediate owner attention**, three further HIGH findings, and a cluster of MEDIUM gaps that are mostly already proportionate to the project's lab/portfolio scale rather than production-grade deficiencies:

- **VS-001 (HIGH→potential CRITICAL):** A plaintext-documented password (`secret123`) for an **ADMIN** account is seeded via Flyway migrations `V2`–`V4`, which run unconditionally under the `prod` profile. `render.yml` deploys this exact profile against a real, operator-managed Postgres instance. **This needs to be verified against the actual Render deployment state immediately** — if that service has ever been deployed with these migrations applied, the admin account is live with a known password.
- **VS-002 (HIGH):** Log sanitization (`LogSanitizer`/`%sanitize`) only works inside `PatternLayout`-based logging (dev/test profiles). The **production** encoder (`LogstashEncoder`, JSON) bypasses it entirely — secrets that reach a log statement in `prod`/`hom`/`prod-vault` are not redacted.
- **VS-003 (HIGH):** The login rate limiter keys on an unauthenticated, client-supplied `X-Forwarded-For` header with no trusted-proxy validation — trivially bypassable, defeating the brute-force control the project built specifically for this purpose.
- **VS-004 (HIGH, dev-scope):** The local Docker Compose stack mounts the host Docker socket into the `portainer` service — full host compromise path if anything reaches that container. Confirmed not to be part of the production (Render) topology.

Everything else — missing `LICENSE` file despite an Apache-2.0 badge, OWASP dependency-check configured but never wired into CI, unpinned `latest` images in Compose, Vault's dynamic DB role being over-privileged, a stale architecture doc, and several LOW/INFO items — is real but proportionate, and several items are already self-tracked by the project's own `CHECKLISTAPPSEC.md`.

**No evidence of a working exploit was produced or required** — all findings come from static code/config reading plus one safe, pre-authorized build command. Live Vault/Docker/E2E validation was intentionally not performed (would require the project's own `ok infra` gate) and is marked NÃO VERIFICADO throughout rather than assumed safe or unsafe.

### Remediation update (2026-10-11)

The four P0 findings have code-side fixes applied and verified against the same pre-authorized gate (`./mvnw -B checkstyle:check test` → `BUILD SUCCESS`, 70/70 tests, 0 Checkstyle violations). Nothing has been committed or pushed; nothing live was started or changed. Status detail is inline on each finding below (§5). Summary:

- **VS-001 — code-side fix applied.** Seed migrations moved to a `dev`-only Flyway location; `prod`/`hom` now only apply schema DDL. **The out-of-band step — checking whether the Render deployment already applied the old seed migrations, and rotating the admin credential if so — still requires the repo owner's own access to the Render dashboard and has not been done from this session.**
- **VS-002 — fixed.** A sanitizing Logstash encoder wrapper now redacts the JSON `message` field in `prod`/`hom`/`prod-vault`, and also suppresses raw structured-argument serialization (a related leak path caught during review) rather than passing it through unfiltered.
- **VS-003 — fixed, with a correction.** The rate limiter's `X-Forwarded-For` trust is now off by default and configurable per profile. An initial version of this fix still read the first (client-controlled) hop even when trust was enabled; a review pass caught that a reverse proxy appends to the *last* position, not the first, so the fix was corrected to use the last hop. `trust-forwarded-header` is enabled only for `prod` (Render is the confirmed sole ingress there); `hom`/`dev` stay at the safe default pending topology confirmation.
- **VS-004 — fixed.** Portainer no longer mounts `/var/run/docker.sock` directly; it goes through a scoped `tecnativa/docker-socket-proxy` instance. Validated with `docker compose config` (render-only); not validated with a live `docker compose up` (no infra authorization in this session).

---

## 2. Scope and methodology

**Branch/commit:** see header. **Components analyzed:** all `src/main/java` (46 files) and `src/test/java` (19 files), all `application*.yml` profiles, 4 Flyway migrations, `pom.xml` (full dependency/plugin tree), `Dockerfile`, `docker-compose.yml`, `docker/observability/*`, `.github/workflows/*`, `.github/dependabot.yml`, `.github/CODEOWNERS`, `scripts/*.sh` (governance/CI-relevant ones), `vault/config/vault.hcl`, Vault bootstrap scripts (read, not executed), all `docs/**` and root-level docs (`README`, `HELP`, `CHANGELOG`, `CONTRIBUTING`, `SECURITY`, `CHECKLISTAPPSEC`), ADRs 0001–0005, `render.yml`.

**Method:** six parallel static-audit passes (architecture/backend; AppSec+Vault; database+tests; CI/CD+Docker+supply chain; observability+resilience+performance; documentation+governance+licensing), each producing evidenced, file:line-cited findings with CONFIRMADO/PROVÁVEL/POTENCIAL/NÃO VERIFICADO status tags — followed by one centrally-run dynamic check and a consolidation pass (this document) that deduplicated overlapping findings (e.g. the rate-limiter bypass was independently found by two passes) and cross-checked the seed-password finding against the actual Render deployment wiring.

**Commands executed:** `git status`/`diff`/`log` (read-only), `find`/`grep`/`Read` across the repo, `java -version`, `./mvnw -v`, `lsof -nP -iTCP -sTCP:LISTEN` (read-only port check), `./mvnw -B checkstyle:check test` (pre-authorized under this repo's own `local-runtime-authorization.md` as a no-infra, read-only-adjacent step). One governance fork used `gh api repos/.../branches/main/protection` (read-only) to verify branch-protection claims.

**Explicitly not run (NÃO VERIFICADO, by design):** `docker compose up` / any live container start, Vault init/unseal/seed against a live instance, integration tests (`-Pintegration-tests`, Testcontainers), `mvnw dependency-check:check` (network/NVD-heavy), any container image scan, any load/benchmark test, any live HTTP probing of a running instance, and anything requiring GitHub web-UI-only visibility (Dependabot alert state, secret-scanning/push-protection status, SonarCloud/Codecov live status).

---

## 3. Technical inventory

| Layer | Evidence | State |
|---|---|---|
| Runtime | Java 17 declared (`pom.xml`), **but active local JDK is 21** (Temurin, via SDKMAN, no project-level `.java-version`/`.sdkmanrc` pin) | PROVÁVEL reproducibility gap — see VS-019 |
| Framework | `spring-boot-starter-parent:3.5.16`, `spring-cloud:2025.0.3` BOM-managed | CONFIRMADO, current supported line per `AGENTS.md` |
| Build | Maven via `mvnw` wrapper; Checkstyle, JaCoCo (report-only, no gate), SonarCloud plugin, OWASP dependency-check (defined, not CI-wired), Flyway plugin | CONFIRMADO |
| Web/API | Spring MVC, 2 controllers (`AuthController`, `UserController`), 3 real endpoints + actuator | CONFIRMADO |
| Security | `spring-boot-starter-security` + `spring-boot-starter-oauth2-resource-server`, stateless JWT (HS256), RBAC (`USER`/`ADMIN`), CSRF disabled (consistent with stateless JWT), CORS restricted | CONFIRMADO |
| Secrets | HashiCorp Vault via `spring-cloud-starter-vault-config` + `...-vault-config-databases`; TOKEN auth in dev Compose only; production (Render) does **not** use Vault | CONFIRMADO |
| Persistence | PostgreSQL + Flyway (4 migrations), Spring Data JPA, 1 entity (`User`) + enum (`UserRole`) | CONFIRMADO |
| Tests | 19 test classes: unit (Mockito), slice (`@SpringBootTest` + H2), integration (`*IT`, Testcontainers + Vault), 1 `@Tag("live")` manual-only test | CONFIRMADO — 67 unit/slice tests pass; `*IT` tests not executed (need Docker) |
| CI/CD | `.github/workflows/maven.yml` (issue-link, dependency-review, quality, integration-tests, docker-build smoke, CodeQL), `release.yml` (tag-triggered release) | CONFIRMADO, Actions 100% SHA-pinned |
| Containers | Multi-stage `Dockerfile` (non-root `appuser`), 7-service dev `docker-compose.yml` (postgres, pgadmin, postgres-exporter, vault, portainer, sonarqube, app) | CONFIRMADO, dev-lab topology — not the production deployment path |
| Observability | Micrometer + Prometheus, OTel tracing bridge, Logback (`logback-spring.xml`, profile-dependent encoder), correlation-ID filter, dedicated `LogSanitizer`/`SensitiveDataConverter`, Grafana/Loki/Tempo/Prometheus configs under `docker/observability/` | CONFIRMADO (config); live data flow NÃO VERIFICADO |
| Deployment | `render.yml` — Render Blueprint, `env: docker`, `SPRING_PROFILES_ACTIVE=prod`, DB creds set manually in Render dashboard (`sync: false`) | CONFIRMADO config; **actual live deployment state NÃO VERIFICADO** (no platform access) |
| Docs | README/HELP/CHANGELOG/CONTRIBUTING/SECURITY/CHECKLISTAPPSEC + `docs/` (architecture, api, configuration, development, 5 ADRs, 11 guides, 8 observability docs) | Mostly consistent with code; one stale doc (VS-010) |
| Governance | Conventional Commits throughout, CODEOWNERS (narrow, supply-chain paths), Dependabot (maven/actions/docker), branch protection on `main` confirmed via `gh api` (required checks: issue-link/quality/integration-tests; 0 required approvals, `enforce_admins: false` — documented solo-maintainer model) | CONFIRMADO |
| License | README/badge claim Apache-2.0; **no `LICENSE` file at repo root** | CONFIRMADO gap — VS-011 |

---

## 4. Architecture map (critical flows)

```
HTTP Request
  → CorrelationIdFilter (HIGHEST_PRECEDENCE)        [assigns/validates X-Correlation-ID → MDC]
  → LoginRateLimitFilter (HIGHEST_PRECEDENCE+10)     [only /api/v1/auth/login; keys on X-Forwarded-For — VS-003]
  → Spring Security filter chain (SecurityConfig)    [deny-by-default; JWT resource-server for protected paths]
  → Controller (AuthController | UserController)
  → Service (AuthService | UserService)
  → Repository (UserRepository, Spring Data JPA)
  → PostgreSQL
```

Trace/log correlation (`correlation_id`, `traceId`, `spanId`) is confirmed continuous from the edge through `GlobalExceptionHandler`; the Repository→PostgreSQL JDBC hop itself is **not confirmed instrumented** (no traced-DataSource wrapper found) — NÃO VERIFICADO.

```
App startup / credential refresh
  → spring-cloud-starter-vault-config  → Vault (TOKEN auth, dev Compose only)
  → database/creds/<role> (dynamic PostgreSQL credentials, leased)
  → VaultDatabaseCredentialRotation (lease renew/expire listener) → HikariCP (graceful soft-evict, not hard kill)
```

This path is **dev/lab-only** — `docs/guides/vault-integration.md` states Compose Vault is local infrastructure only, and `render.yml`/`application-prod.yml` show production using static env-var datasource credentials, not Vault. This materially narrows the blast radius of the Vault-specific findings (VS-008, VS-009, VS-014).

---

## 5. Prioritized findings catalog

Format per finding: ID · Severity · Confidence · Status · Domain · Evidence · Scenario · Impact · Recommendation · Effort. Severities follow the audit's own CRITICAL/HIGH/MEDIUM/LOW/INFO model (impact × exploitability × exposure, not raw CVSS). Status: CONFIRMADO (direct evidence), PROVÁVEL (strong partial evidence), POTENCIAL (plausible, condition-dependent), NÃO VERIFICADO (needs live/external access).

### P0 — act now

**VS-001 — Plaintext-documented ADMIN password shipped via unconditional Flyway migrations, wired to a real deploy target**
- **Severidade:** HIGH, escalates to **CRITICAL if confirmed live** · **Confiança:** Alta (mechanism), Não verificado (current live exposure)
- **Domínio:** Database / Secrets management · **Status:** CONFIRMADO (mechanism) / POTENCIAL (real-world impact)
- **Evidência:** `src/main/resources/db/migration/V2__insert_users_table.sql` (10 seeded users, shared BCrypt hash) → `V3__align_seed_passwords.sql` (comment documents the hash resolves to a known password) → `V4__add_user_roles.sql:4-6` (grants `ADMIN` to `john@example.com`) → `src/test/java/.../SeedPasswordHashTest.java:14,18,21-22` (proves the hash ⇒ `secret123` in plain text, committed to the repo) → `src/main/resources/application-prod.yml:19-22` and `application-hom.yml:10-12` (`flyway.enabled: true`, default `classpath:db/migration` location — no prod-specific exclusion of seed data) → `render.yml:9-17` (`SPRING_PROFILES_ACTIVE=prod`, real Postgres credentials configured in the Render dashboard, `sync: false`).
- **Cenário:** On first boot against any real database with this migration path applied, the app creates an `ADMIN` account (`john@example.com` / `secret123`) that is fully documented in git history and a test file.
- **Impacto:** If the Render service has been deployed at any point with these migrations applied against the real database, this is an active admin-level credential compromise, publicly discoverable by anyone who reads the repository.
- **Causa-raiz:** No environment-gated Flyway migration path separating demo/seed fixtures from real schema DDL.
- **Recomendação (imediata):**
  1. **Verify now, out-of-band:** check whether the Render `vaultspring` service has ever run with these migrations against its live database. If yes, **rotate/disable that account and change its password immediately**, independent of any code fix.
  2. Split seed inserts into a `dev`-only Flyway location (`spring.flyway.locations` per profile) so `prod`/`hom` only ever apply schema DDL, never demo data.
  3. If demo data must exist in a shared/homologation environment, generate a random password per deploy (e.g. from a secret) rather than a hardcoded, documented one.
- **Teste de regressão:** Add a test asserting `prod`/`hom` Flyway locations contain no seed-insert scripts.
- **Critério de aceite:** No seed/demo data reachable via the `prod`/`hom` Flyway location; the Render verification step above has been completed and documented.
- **Esforço:** S (migration split) + **immediate out-of-band verification, independent of code effort**.
- **Status de remediação (2026-10-11):** Code-side fix applied — `V2__insert_users_table.sql`/`V3__align_seed_passwords.sql` moved to [`src/main/resources/db/migration/dev/`](../../src/main/resources/db/migration/dev); `prod`/`hom` `spring.flyway.locations` now `classpath:db/migration` only, with `ignore-migration-patterns: ["2:missing","3:missing"]` added so a deploy that already applied the old seed versions won't fail validate (scoped to those two versions only — a genuinely lost future migration still fails validate). **Out-of-band verification/rotation on the real Render deployment has NOT been done — still owner-only.**

**VS-002 — Production log encoder bypasses secret redaction**
- **Severidade:** HIGH · **Confiança:** Alta · **Domínio:** Observability/AppSec · **Status:** CONFIRMADO
- **Evidência:** `src/main/resources/logback-spring.xml:8-20` routes `prod | prod-vault | hom` through `net.logstash.logback.encoder.LogstashEncoder` (a JSON provider-based encoder), while `%sanitize` (`SensitiveDataConverter` → `LogSanitizer`) is a `conversionRule` that only takes effect inside a `PatternLayout` string, used solely in the `dev | test | default` block (`:22-31`). `docs/observability/02-logging.md:26-34` documents redaction as a blanket guarantee without this caveat.
- **Cenário:** Any log statement in `prod`/`hom`/`prod-vault` that happens to include an `Authorization` header, a password field, or a Vault token (e.g. via an exception message or a future debug log) is emitted unredacted in the environments that matter most.
- **Impacto:** Secret leakage into log aggregation (and whatever retains/ships those logs) specifically in production, while the non-production profiles are protected — the opposite of what's needed.
- **Recomendação:** Add a custom Logstash JSON provider (or a `MessageJsonProvider` override) that runs the rendered message through `LogSanitizer` before serialization; add a test that renders a sample "Authorization: Bearer xxx" line through the prod encoder and asserts redaction.
- **Teste de regressão:** JSON output for the `prod` profile must not contain a literal bearer/password/vault-token value for a crafted log line.
- **Critério de aceite:** Same red-team log line produces `[REDACTED]` in both dev (pattern) and prod (JSON) output.
- **Esforço:** S–M.
- **Status de remediação (2026-10-11):** Fixed — [`SanitizingLogstashEncoder`](../../src/main/java/io/github/kleilsonsantos/security/vaultspring/observability/SanitizingLogstashEncoder.java) + [`SanitizingLoggingEventWrapper`](../../src/main/java/io/github/kleilsonsantos/security/vaultspring/observability/SanitizingLoggingEventWrapper.java) now wraps every event before the JSON encoder serializes it, routing the message through `LogSanitizer` and suppressing raw structured-argument serialization (`getArgumentArray()` → `null`) — the latter caught by review as a parallel leak path the message-only fix didn't cover.

**VS-003 — Login rate limiter is bypassable via unverified `X-Forwarded-For`**
- **Severidade:** HIGH · **Confiança:** Alta · **Domínio:** AppSec · **Status:** CONFIRMADO (independently found by 2 of 6 audit passes)
- **Evidência:** `src/main/java/.../security/LoginRateLimitService.java:57-64` — `resolveClientKey` reads the first hop of the client-supplied `X-Forwarded-For` header verbatim, with no trusted-proxy allowlist, no `ForwardedHeaderFilter`, and no cross-check against `request.getRemoteAddr()`.
- **Cenário:** An attacker sends `POST /api/v1/auth/login` with a different `X-Forwarded-For` value on every request → each lands in a fresh rate-limit bucket → unlimited credential-stuffing throughput, fully defeating the control the project built and tracks specifically for brute-force protection (checklist "SEC-002").
- **Impacto:** The brute-force control is non-functional against a motivated attacker regardless of deployment topology, since the header is trusted unconditionally even if no reverse proxy is in front of the app.
- **Recomendação:** If no trusted reverse proxy sets `X-Forwarded-For` itself, key the limiter on `request.getRemoteAddr()` only. If a proxy is introduced later, use Spring's native `server.forward-headers-strategy=framework` or restrict trust to a known proxy CIDR before honoring the header.
- **Teste de regressão:** Varying `X-Forwarded-For` across requests from the same simulated remote address must not reset/bypass the attempt counter.
- **Critério de aceite:** N+1 attempts from one real client return `429` regardless of attacker-controlled `X-Forwarded-For` values.
- **Esforço:** S.
- **Referências:** OWASP ASVS 4.0 §11.1 (anti-automation).
- **Status de remediação (2026-10-11):** Fixed, with a correction mid-fix. `vaultspring.security.login-rate-limit.trust-forwarded-header` (default `false`) now gates whether the header is used at all. The first fix attempt still took the *first* comma-separated hop when trust was enabled — which remains client-controlled even behind a real reverse proxy, since the proxy appends its observed address rather than replacing the header. Corrected to use the *last* hop instead. Enabled (`true`) only for `prod` (`application-prod.yml`; Render confirmed as sole ingress); `hom`/`dev` remain at the safe default. Regression tests added: spoofed/rotating `X-Forwarded-For` no longer bypasses the limiter.

**VS-004 — Portainer container mounts the host Docker socket (dev Compose only)**
- **Severidade:** HIGH (scoped to local dev) · **Confiança:** Alta · **Domínio:** Docker/Infra · **Status:** CONFIRMADO; **NÃO APLICÁVEL to production** (Render topology does not include this Compose stack)
- **Evidência:** `docker-compose.yml:88-89` — `/var/run/docker.sock:/var/run/docker.sock` bind-mount on the `portainer` service, exposed on host port `9001`.
- **Cenário:** Anything that reaches the Portainer UI/API on a shared or exposed network gains root-equivalent control of the Docker host (arbitrary privileged containers, reading other containers' env/secrets, host filesystem access via a mounted container).
- **Impacto:** Full local host compromise path — relevant given `AGENTS.md`/`local-runtime-authorization.md` explicitly treat this machine as a **shared MacBook**.
- **Recomendação:** Replace the direct socket mount with a scoped socket proxy (e.g. `tecnativa/docker-socket-proxy`) limited to the endpoints Portainer actually needs, or make Portainer an opt-in Compose profile rather than default-on.
- **Esforço:** S.
- **Status de remediação (2026-10-11):** Fixed — `docker-compose.yml` now routes Portainer through a `tecnativa/docker-socket-proxy:v0.4.2` service instead of mounting the host socket directly; only `CONTAINERS`, `IMAGES`, `NETWORKS`, `VOLUMES`, `EXEC`, `INFO`, `PING`, `VERSION`, `POST` are enabled on the proxy. Validated with `docker compose config` only (render, no `up` — no infra authorization in this session).

### P1 — security/reliability essentials

**VS-005 — OWASP dependency-check defined but never executed by CI**
- Severidade: MEDIUM · Confiança: Alta · Domínio: Supply chain/CI · Status: CONFIRMADO
- Evidência: `pom.xml:367-392` defines a `dependency-check` profile (`failBuildOnCVSS=7`); no job in `.github/workflows/maven.yml` activates `-Pdependency-check`. Only PR-diff `dependency-review-action` and CodeQL (SAST, not SCA) run automatically.
- Cenário: A CVE disclosed post-merge in an already-present (non-diff) dependency is never surfaced except by a future Dependabot PR — the running `main`/`sandbox` state has a detection gap between merges.
- Recomendação: Add a scheduled (e.g. weekly) CI job running `-Pdependency-check` with an NVD API key secret; keep non-blocking initially if NVD rate limits are a concern.
- Esforço: S–M (needs NVD API key provisioning).

**VS-006 — Several Docker Compose images are unpinned (`latest`/no tag)**
- Severidade: MEDIUM · Confiança: Alta · Domínio: Docker · Status: CONFIRMADO
- Evidência: `docker-compose.yml:63` `hashicorp/vault:latest`; `:30` `dpage/pgadmin4:latest`; `:48` postgres-exporter (no tag); `:82` portainer (no tag).
- Cenário: A breaking or compromised upstream image lands silently on the next pull/rebuild; no reproducibility between two "identical" runs.
- Recomendação: Pin explicit versions (ideally digests); the existing Dependabot `docker` ecosystem config already covers bumping these.
- Esforço: S.
- **Status de remediação (2026-10-11):** Fixed — all four images pinned to the current stable version confirmed directly against the Docker Hub / Quay.io registry APIs (not a third-party aggregator): `hashicorp/vault:2.1.2`, `dpage/pgadmin4:9.18.0`, `quay.io/prometheuscommunity/postgres-exporter:v0.20.1`, `portainer/portainer-ce:2.45.2`. Note: `hashicorp/vault` jumped from the 1.x series to 2.x upstream — `latest` already resolved to `2.1.2` at pin time (confirmed via matching push timestamps), so this reflects current real behavior rather than a deliberate major-version upgrade; flagged in a `docker-compose.yml` comment to re-run the Vault bootstrap scripts under `ok infra` before relying on it.

**VS-007 — JWT has no `iss`/`aud` claims or revocation path (already self-tracked)**
- Severidade: MEDIUM · Confiança: Alta · Domínio: AppSec · Status: CONFIRMADO; already listed open in `CHECKLISTAPPSEC.md`
- Evidência: `security/JwtService.java:47-63` sets only `sub/iat/exp/uid/name/roles`; `config/JwtConfig.java:48-53` registers no custom `OAuth2TokenValidator`; no revocation store exists.
- Impacto: Bounded today — HS256 secret is service-local, default TTL is 1h, no cross-service token reuse risk currently. Becomes relevant only if the token is ever trusted by another service or long-lived-session/logout semantics are required.
- Recomendação: Add `iss`/`aud` + matching validators if the trust boundary ever widens; consider short-TTL + refresh or `jti`-based revocation if forced logout becomes a requirement. Not urgent now.
- Esforço: M (if implemented).

**VS-008 — Vault dynamic database role is over-privileged (`ALL PRIVILEGES`, not least-privilege verbs)**
- Severidade: MEDIUM · Confiança: Alta · Domínio: Vault/DB · Status: CONFIRMADO
- Evidência: `scripts/vault-seed-database-dev.sh:38-42` — `creation_statements` grant `ALL PRIVILEGES ON ALL TABLES`/`ALL SEQUENCES IN SCHEMA public`, while the app only needs CRUD on `users`.
- Cenário: A leaked short-lived dynamic credential can `DROP`/`TRUNCATE`/`ALTER` any table in `public`, not just read/write what the app actually touches.
- Recomendação: Scope to `GRANT SELECT, INSERT, UPDATE, DELETE` (+ `USAGE, SELECT` on sequences), dropping DDL/ownership rights.
- Esforço: S. (This is the only role template in the repo — worth fixing even though it's dev-scope today, since it's the de-facto pattern for any future shared environment.)
- **Status de remediação (2026-10-11):** Fixed — `creation_statements` in [`scripts/vault-seed-database-dev.sh`](../../scripts/vault-seed-database-dev.sh) now grants `SELECT, INSERT, UPDATE, DELETE` on tables and `USAGE, SELECT` on sequences only (no DDL/ownership). Sequence grant kept deliberately (rather than dropped) since the official Postgres docs don't explicitly confirm whether `GENERATED ALWAYS AS IDENTITY` columns need it — keeping it is the safe choice (low risk, avoids guessing). Not live-tested against a real Vault/Postgres (no infra authorization in this session).

**VS-009 — Vault dev bootstrap uses a long-lived TOKEN (root token), not AppRole (dev-scope, already self-tracked)**
- Severidade: LOW (current blast radius) · Confiança: Alta · Domínio: Vault · Status: CONFIRMADO as dev-lab characteristic; NÃO APLICÁVEL to production
- Evidência: `application-vault.yml:14-15` (`authentication: TOKEN`), `scripts/vault-init-dev.sh` (single unseal key share, root token exported to app). Already tracked unchecked in `CHECKLISTAPPSEC.md`.
- Recomendação: No action needed for current scope; adopt AppRole with a scoped policy before ever pointing this bootstrap pattern at a shared/staging Vault.
- Esforço: N/A now; M if adopted.

**VS-010 — `docs/architecture.md` is stale relative to the shipped JWT/RBAC implementation**
- Severidade: MEDIUM · Confiança: Alta · Domínio: Documentation/Governance · Status: CONFIRMADO
- Evidência: `docs/architecture.md:118,163-167` still frames `/api/v1/**` as public and JWT as "out of scope (#6)", contradicted by `SecurityConfig.java:109-121`, `docs/api.md:14-47` (full, accurate RBAC table), `README.md:51-53`, `AGENTS.md:7`.
- Cenário: A reader relying only on `architecture.md` starts from a materially wrong threat model.
- Recomendação: Update the security-model section to match `SecurityConfig.java`/`docs/api.md`; remove the stale "out of scope" line.
- Esforço: S.
- **Status de remediação (2026-10-11):** Fixed — [`docs/architecture.md`](../architecture.md)'s security-model section, diagrams, and package table now match `SecurityConfig.java` exactly (JWT login, `/users/me` authenticated, `/users` admin-only, deny-by-default); the stale "JWT out of scope (#6)" line was removed from "Out of scope" and replaced with the still-genuinely-open `iss`/`aud`/revocation gap (VS-007).

**VS-011 — No `LICENSE` file despite an Apache-2.0 badge/claim**
- Severidade: MEDIUM · Confiança: Alta · Domínio: Governance/Licensing · Status: CONFIRMADO
- Evidência: `README.md:10,112-114` claims Apache 2.0; no `LICENSE`/`LICENSE.*` file exists at the repo root.
- Impacto: Legally, no license is actually in force absent the file — GitHub's own license detector will show "no license," contradicting the badge, and anyone trying to reuse/fork the code has no actual grant.
- Recomendação: Add the Apache-2.0 `LICENSE` text (and `NOTICE` if applicable) at the repo root.
- Esforço: S.
- **Status de remediação (2026-10-11):** Fixed — [`LICENSE`](../../LICENSE) added at repo root with the literal Apache License 2.0 text, downloaded directly from `https://www.apache.org/licenses/LICENSE-2.0.txt` and diff-verified byte-for-byte against the project's copy (terms unmodified; only the copyright block filled in: `Copyright 2025-2026 Kleilson Santos`, years taken from the actual first/last commit dates, not guessed).

### P2 — structural quality

**VS-012 — TOCTOU race on duplicate-email user creation (likely surfaces as 500 instead of 409)**
- Severidade: MEDIUM · Confiança: Média · Domínio: Backend · Status: POTENCIAL (not executed)
- Evidência: `service/UserService.java:69-77` — `existsByEmail` check then `save`, no catch for `DataIntegrityViolationException`; DB `UNIQUE` constraint exists (`V1__create_users_table.sql:5`).
- Recomendação: Catch the constraint violation and map it to the same 409 Problem Detail, or drop the pre-check and rely solely on the constraint + catch.
- Esforço: S.

**VS-013 — Prometheus alert rules have no configured Alertmanager receiver**
- Severidade: LOW-MEDIUM · Confiança: Alta · Domínio: Observability · Status: CONFIRMADO
- Evidência: `docker/observability/prometheus/prometheus.yml:26-27` — `alertmanagers: []`; 4 real rules exist in `rules/alerts.yml` referencing real metrics, but nothing routes a firing alert to a human.
- Recomendação: Wire an Alertmanager route, or explicitly document alerting as "Prometheus-UI-only, no paging" so it isn't mistaken for an on-call signal.
- Esforço: S–M.

**VS-014 — `app` depends on Vault via `service_started`, not a health gate; Vault has no healthcheck**
- Severidade: MEDIUM · Confiança: Média · Domínio: Docker · Status: PROVÁVEL (not live-tested)
- Evidência: `docker-compose.yml:62-79` (no `healthcheck` on `vault`), `:127-131` (`app.depends_on.vault.condition: service_started`).
- Cenário: `vault` container process starting ≠ Vault unsealed/ready; `spring-cloud-starter-vault-config` bootstrap could race a not-yet-ready Vault.
- Recomendação: Add a Vault healthcheck (`vault status` or `/v1/sys/health`) and switch `app`'s condition to `service_healthy`. Good Fase-16 dynamic-validation candidate under explicit `ok infra`.
- Esforço: S.

**VS-015 — No container image vulnerability scan in CI**
- Severidade: LOW · Confiança: Alta · Domínio: CI/Supply chain · Status: CONFIRMADO
- Evidência: `.github/workflows/maven.yml:167-175` — `docker build` smoke test only, no scan/push.
- Recomendação: Add a non-blocking image-scan step (e.g. Trivy) after build.
- Esforço: S.

**VS-016 — No resource limits or app-level healthcheck on most Compose services**
- Severidade: LOW · Confiança: Alta · Domínio: Docker · Status: CONFIRMADO
- Evidência: only `postgres` has a `healthcheck`; none have `mem_limit`/`cpus`/`deploy.resources` — notable given the explicit shared-MacBook constraint in this repo's own gate docs.
- Recomendação: Add conservative memory caps (especially `sonarqube`, known heavy) and an `app` `/actuator/health` healthcheck.
- Esforço: S.

**VS-017 — No circuit breaker / bounded retry around Vault or DB dependency failures**
- Severidade: LOW (proportional to current scale) · Confiança: Alta · Domínio: Resilience · Status: POTENCIAL
- Evidência: no resilience4j or equivalent dependency; `VaultDatabaseCredentialRotation` catches rotation failures, logs, increments a counter, but has no self-healing retry until the next natural lease event.
- Recomendação: Not urgent for a single-instance portfolio-scale service; document as an accepted limitation rather than leaving it silent.
- Esforço: N/A (documentation) or M if implemented.

**VS-018 — No catch-all exception handler for unmapped exceptions**
- Severidade: LOW · Confiança: Média · Domínio: Backend · Status: PROVÁVEL
- Evidência: `exception/GlobalExceptionHandler.java:32-59` only handles `MethodArgumentNotValidException`/`ResponseStatusException`; an unmapped exception falls through to Spring Boot's default error controller, which may not match the RFC 7807 contract used elsewhere.
- Recomendação: Add `@ExceptionHandler(Exception.class)` → generic `ProblemDetail` 500, logged via the existing safe-logging path.
- Esforço: S.

**VS-019 — Active local JDK (21) differs from the pom-declared toolchain (17), with no project-level pin**
- Severidade: LOW · Confiança: Alta · Domínio: Reproducibility · Status: CONFIRMADO
- Evidência: `pom.xml` `<java.version>17</java.version>`; local `java -version` → Temurin 21.0.11 (SDKMAN), no `.sdkmanrc`/`.java-version` file in the repo.
- Cenário: Different contributors/CI runners could silently compile/test against different JDK major versions; this audit's own build ran on 21, not 17.
- Recomendação: Add a `.sdkmanrc` (or Maven toolchains) pinning JDK 17 for local reproducibility, matching CI's declared version.
- Esforço: S.

### P3 — maturity / low-risk cleanup

**VS-020 — Dockerfile `ENTRYPOINT` uses exec form with a shell-style `${VAR}` that may not expand**
- Severidade: LOW · Confiança: Baixa · Domínio: Docker · Status: NÃO VERIFICADO (needs a live run to confirm)
- Evidência: `Dockerfile:33` — `ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]` (JSON/exec form, which Docker does not shell-expand).
- Recomendação: Verify with a live `docker run` + `/actuator/env` or an active-profile log line (Fase-16 candidate under `ok infra`); if unresolved, switch to shell form or an explicit `JAVA_TOOL_OPTIONS` env var.
- Esforço: S (verification only).

**VS-021 — Unused `caffeine` dependency**
- Severidade: INFO · Confiança: Alta · Domínio: Supply chain · Status: CONFIRMADO
- Evidência: `pom.xml:126-127` declares it; no usage found anywhere in `src/main/java`.
- Recomendação: Remove, or wire it to whatever it was intended for (e.g. a rate-limiter cache).
- Esforço: S.

**VS-022 — `GET /api/v1/users` has no pagination**
- Severidade: INFO · Confiança: Alta · Domínio: API design · Status: CONFIRMADO
- Evidência: `controller/UserController.java:41-44`, `service/UserService.java:45-48` return the full list; endpoint is `ADMIN`-only, current seed size is small.
- Recomendação: Add `Pageable` support when/if the table grows. Not urgent now.
- Esforço: S.

**VS-023 — Potential timing side-channel for user enumeration on login**
- Severidade: LOW · Confiança: Média · Domínio: AppSec · Status: POTENCIAL/NÃO VERIFICADO (no timing measurement performed)
- Evidência: `service/AuthService.java:64-81` — unknown-email path skips the BCrypt comparison entirely; known-email-wrong-password path runs it. Both return an identical generic 401 body, but latency likely differs.
- Recomendação: Low priority; if in-scope, perform a dummy BCrypt comparison on the not-found path to equalize timing.
- Esforço: S, low priority.

**VS-024 — JaCoCo is wired for reporting only, not enforced as a coverage gate**
- Severidade: LOW · Confiança: Alta · Domínio: Test quality · Status: CONFIRMADO
- Evidência: `pom.xml:237-259` binds only `prepare-agent`/`report`, no `check` execution with `<rules>`.
- Recomendação: Add a modest, evidence-based `jacoco:check` threshold if enforcement is desired, or document coverage as informational-only.
- Esforço: S.

**VS-025 — Side-effecting lease-listener registration inside a constructor**
- Severidade: INFO · Confiança: Média · Domínio: Backend style · Status: CONFIRMADO
- Evidência: `config/VaultDatabaseCredentialRotation.java:51-69` registers `leaseContainer.addLeaseListener(...)` in the constructor rather than `@PostConstruct`.
- Recomendação: Optional — move to `@PostConstruct` to separate construction from initialization side effects. Not blocking.
- Esforço: S.

**Informational, no action required:**
- `CODEOWNERS` exists but isn't enforced by branch protection (`require_code_owner_reviews: false`, `enforce_admins: false`) — correct and intentional for a solo-maintainer repo; revisit only if a second maintainer joins.
- `Makefile` contains offensive-security test targets (`sqlmap`, `ab`-based load test, `zap-scan`) — all hardcoded to `localhost`, correctly scoped as dev-only tooling; not executed during this audit and no evidence of misuse.
- Production HikariCP pool is small (`maximum-pool-size: 5`) — plausible for a small/free-tier Postgres instance; no measured concurrency problem, not flagged as a defect without load data.

---

## 6. Coverage matrix

| Dimension | Applicable? | Evidence collected | Verification state |
|---|---|---|---|
| Architecture/boundaries | Yes | Full package read, ADR cross-check | CONFIRMADO |
| AppSec/threat model | Yes | Full SecurityConfig/JWT/rate-limit read, authz matrix built | CONFIRMADO (static); dynamic probing NÃO VERIFICADO |
| Vault/secrets | Yes | Config + bootstrap scripts read (not executed) | CONFIRMADO (static); live rotation/lease behavior NÃO VERIFICADO |
| Database/Flyway | Yes | All 4 migrations + entity/repo read | CONFIRMADO |
| Tests/quality | Yes | All 19 test files read; 67 unit tests executed and passed | CONFIRMADO (unit); `*IT` tests NÃO VERIFICADO (need Docker) |
| CI/CD | Yes | Both workflows fully read | CONFIRMADO (config); live run behavior/required-checks enforcement partially confirmed via `gh api` |
| Docker/infra | Yes | Dockerfile + both compose files read | CONFIRMADO (static); live boot behavior NÃO VERIFICADO |
| Supply chain | Yes | Full `pom.xml` dependency/plugin tree read | CONFIRMADO (versions); **no CVE scan performed** — explicitly NÃO VERIFICADO |
| Observability | Yes | Observability package + Logback + Grafana/Prometheus configs read | CONFIRMADO (config/code); live telemetry flow NÃO VERIFICADO |
| Resilience | Yes | Credential-rotation and rate-limit code read | CONFIRMADO (code); failure-injection behavior NÃO VERIFICADO |
| Performance | Proportional | Pool/cache config read | CONFIRMADO (config); no benchmarks run (correctly out of scope) |
| Documentation | Yes | All docs/ + root docs read | CONFIRMADO |
| Governance/traceability | Yes | Commit history, CODEOWNERS, Dependabot, branch protection (`gh api`) | CONFIRMADO |
| Licensing | Yes | Root + README claims checked | CONFIRMADO gap found |
| Container/image scanning | Partially | Static Dockerfile review only | NÃO VERIFICADO (no scan tool run) |
| Live deployment state (Render) | No | `render.yml` + prod config read | **NÃO VERIFICADO — no platform access; see VS-001** |

---

## 7. Metrics actually observed

- Unit tests: **67 run, 0 failures, 0 errors, 0 skipped** (`mvnw -B checkstyle:check test`, 3m27s wall time).
- Checkstyle: **0 violations** (build did not fail at the `checkstyle:check` goal).
- Main source files: 46 (`.java`, excluding `package-info.java` boilerplate: 40 substantive classes).
- Test source files: 19.
- Flyway migrations: 4.
- GitHub Actions workflows: 2, both with 100% SHA-pinned `uses:` references.
- JaCoCo line/branch coverage %: **not measured in this audit** (no report artifact generated/read) — do not infer a number.

---

## 8. Remediation plan (priority order)

| Priority | Finding | Effort | Dependencies | Acceptance evidence | Status (2026-10-11) |
|---|---|---|---|---|---|
| P0 | VS-001 verify Render deployment state + rotate if live | — (out-of-band) | None — do this independent of any code change | Confirmation the admin account is not live with a known password, or has been rotated | ⬜ **Pending — owner-only, requires Render dashboard access** |
| P0 | VS-001 split seed migrations by profile | S | — | `prod`/`hom` Flyway location contains no seed inserts | ✅ Done |
| P0 | VS-002 fix prod log redaction | S–M | — | Crafted secret-bearing log line redacted in prod JSON output | ✅ Done |
| P0 | VS-003 fix rate-limit key resolution | S | — | Bypass test fails to defeat the limiter | ✅ Done |
| P0 | VS-004 remove/scope Portainer socket mount | S | — | No service has direct `docker.sock` access | ✅ Done (not live-validated — no `ok infra`) |
| P1 | VS-005 wire dependency-check into a scheduled CI job | S–M | NVD API key secret | Job runs and produces a report on schedule | ⬜ Deferred — needs owner-provisioned NVD API key |
| P1 | VS-006 pin Compose image versions | S | — | No `latest`/untagged images remain | ✅ Done |
| P1 | VS-008 scope Vault DB role to least privilege | S | — | Dynamic role cannot DDL | ✅ Done (not live-tested) |
| P1 | VS-010 update `architecture.md` | S | — | Matches `SecurityConfig.java`/`docs/api.md` | ✅ Done |
| P1 | VS-011 add `LICENSE` file | S | — | File present, matches badge | ✅ Done |
| P2 | VS-012, VS-013, VS-014, VS-015, VS-016, VS-018, VS-019 | S each | VS-014/VS-020 need `ok infra` to verify | Per-finding acceptance criteria above |
| P3 | VS-007, VS-009, VS-017, VS-020–VS-025 | S–M | Mostly already self-tracked or low priority | Per-finding acceptance criteria above |

---

## 9. Residual risks

| Risk | Justification | Compensating control | Re-evaluate when |
|---|---|---|---|
| Live exposure of VS-001 | No platform access to confirm | None currently known | Immediately — this is the one item that should not wait for a prioritized sprint |
| JWT has no revocation (VS-007) | Service-local HS256, 1h TTL, no cross-service trust today | Short TTL bounds exposure window | If the token is ever trusted by another service, or forced-logout becomes a requirement |
| Vault TOKEN/root-token bootstrap (VS-009) | Dev-Compose-only, not production | Documented as local-only in `vault-integration.md` | If a shared/staging Vault is ever introduced |
| No circuit breaker on Vault/DB failure (VS-017) | Single-instance, portfolio-scale service | Lease-listener failure counter + next natural lease event | If the service moves to multi-instance or higher-availability requirements |
| No live CVE scan performed | Out of this audit's safe/fast scope | Dependabot (weekly maven ecosystem) partially compensates | Once VS-005 is wired, or before any production-sensitive release |

---

## 10. Technical conclusion

- **Local reproducible execution:** Yes, with a caveat — JDK version drift (VS-019) should be pinned.
- **Automated validation:** Partially — unit-level validation is real and green (67/67); integration/live validation requires infra authorization not exercised in this audit.
- **Technical demonstration (as a Secure Backend Engineering portfolio piece):** Yes — the project demonstrates genuine, non-trivial controls (deny-by-default security chain, dynamic Vault-issued DB credentials with graceful rotation, fail-fast JWT secret guard, structured+correlated logging, SHA-pinned CI with SAST). This is well above typical CRUD-demo quality.
- **Safe evolution:** Yes, with the P0 items addressed first — none of them require an architectural rework, all are scoped, small fixes.
- **Operation in a controlled environment:** Yes, as currently scoped (dev Compose Vault is explicitly local-only).
- **Production readiness:** **Not yet** — specifically blocked on verifying/resolving VS-001 and fixing VS-002/VS-003 first. No blanket "production-ready" label is warranted until those are closed and confirmed.

---

## 11. Next steps (recommended order)

1. ~~Fix the production log-redaction gap (VS-002) and the rate-limiter bypass (VS-003).~~ **Done 2026-10-11** (code-side; build green, 70/70 tests).
2. ~~Remove/scope the Portainer Docker-socket mount (VS-004).~~ **Done 2026-10-11** (config-side; not live-validated).
3. ~~Split seed data out of the `prod`/`hom` Flyway path (VS-001 code-side fix).~~ **Done 2026-10-11.**
4. **Still outstanding, owner-only:** verify whether the Render `vaultspring` deployment has ever applied the old Flyway seed migrations against its real database; rotate the admin credential if so (VS-001's out-of-band step — no code change can close this).
5. Work through P1 (dependency-check CI job, image pinning, Vault least-privilege DB role, stale doc, LICENSE file).
6. Schedule a follow-up session with `ok infra` authorization to run the integration-test suite, live-validate the Portainer socket-proxy swap and the Vault healthcheck/readiness ordering (VS-014), and confirm the Dockerfile `ENTRYPOINT` profile resolution (VS-020) — these are the remaining NÃO VERIFICADO items that need live infra, not more static reading.

---

*This report was produced by six parallel static-audit passes plus one pre-authorized build/test run, consolidated and cross-checked by the coordinating session. No findings were invented; every claim above is cited to a specific file/line. Items marked NÃO VERIFICADO genuinely were not checked and should not be treated as either confirmed-safe or confirmed-vulnerable.*

# Security Policy

## Supported versions

| Version | Supported |
| ------- | --------- |
| 0.1.x   | Yes       |

## Reporting a vulnerability

**Do not open public issues for security vulnerabilities.**

Prefer [GitHub Security Advisories](https://github.com/KleilsonSantos/VaultSpring/security/advisories/new) or email **kleilson@icloud.com**.

Include a description, reproduction steps, impact, and suggested mitigation (if any).

We aim to respond within 5 business days.

## GitHub Security posture

| Feature | Expected posture | Notes |
| ------- | ---------------- | ----- |
| Dependabot alerts | On | Complements OWASP Dependency-Check (`-Pdependency-check`) |
| Dependabot version updates | On → `main` | See `.github/dependabot.yml` |
| Secret scanning + push protection | On | Block accidental secret commits |
| Code scanning (CodeQL) | On via CI workflow only | `java-kotlin`, CodeQL Action v4 — **disable** GitHub Default CodeQL setup to avoid duplicate analysis |
| Spring Cloud Vault | On (profile `vault`) | KV v2 via `spring-cloud-starter-vault-config` 2025.0.x |
| OWASP Dependency-Check | Optional Maven profile | `./mvnw verify -Pdependency-check` (local/scheduled; not duplicated in CI) |

Owner checklist: repo **Settings → Code security**.

## Secrets in this project

- Never commit `.env`, Vault root tokens, or `vault/data/`
- Production datasource credentials must come from the environment or Vault profile (see `application-prod.yml`, `application-vault.yml`)
- Compose Vault is **local infrastructure**; use `VAULT_TOKEN` via env — never commit tokens or unseal keys

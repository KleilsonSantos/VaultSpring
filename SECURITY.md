# Security Policy

## Supported versions

| Version | Supported |
| ------- | --------- |
| 0.1.x   | ✅        |

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
| Code scanning (CodeQL) | On via CI | `java-kotlin`, CodeQL Action v4 |
| OWASP Dependency-Check | Optional profile | `./mvnw verify -Pdependency-check` |

Owner checklist: repo **Settings → Code security**.

## Secrets in this project

- Never commit `.env`, Vault root tokens, or `vault/data/`
- Production datasource credentials must come from the environment (see `application-prod.yml`)
- Compose Vault is **local infrastructure**; the Spring app does not yet use `spring-cloud-vault`

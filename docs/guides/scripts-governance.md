# Scripts governance and CI supply chain

Repository shell scripts are **pipelines-as-code** (NIST SSDF PO.3). They are not a security anti-pattern when governed; **ungoverned** script changes are a [Poisoned Pipeline Execution (PPE)](https://owasp.org/www-project-top-10-ci-cd-security-risks/CICD-SEC-04-Poisoned-Pipeline-Execution) vector (OWASP CICD-SEC-4).

VaultSpring treats scripts like **privileged tools**: classified, reviewed, and least-privilege at runtime.

## Threat model (summary)

| Threat | Mitigation in this repo |
| ------ | ------------------------ |
| Malicious script merged to `sandbox`/`main` | PR review, required CI checks, [`.github/CODEOWNERS`](../../.github/CODEOWNERS) |
| Script runs with broad CI secrets | Job-scoped `permissions`; fork PRs do not receive repository secrets |
| Secret exfiltration via script | No secrets in script bodies; `check-secrets.sh` + GitGuardian |
| Unsafe shell (`eval`, unquoted input) | `set -euo pipefail`; no `eval` in security gates; ShellCheck in CI |
| Mutable third-party Actions | Workflows pin actions to **full commit SHAs** ([GitHub secure use](https://docs.github.com/en/actions/reference/security/secure-use)) |
| Local infra abuse by agents | [`local-runtime-authorization.md`](./local-runtime-authorization.md) — Tier D requires **`ok infra`** |

## Script tiers

| Tier | Runs where | Examples | Owner gate |
| ---- | ---------- | -------- | ---------- |
| **A — CI-critical** | GitHub Actions on PR/push | `check-pr-issue-link.sh`, `validate-observability-config.sh`, `check-pr-delivery-gate-selftest.sh`, `check-semver-alignment.sh` | CODEOWNERS + green CI |
| **B — Security enablers** | Local pre-push (mirrors CI) | `pre-push-check.sh`, `check-secrets.sh`, `check-pr-delivery-gate.sh`, `check-pkb-inventory.sh` | Developer machine; step 4b before push |
| **C — Delivery / DX** | Local git workflow | `task-kickoff.sh`, `bootstrap-sandbox.sh`, `install-hooks.sh`, `check-version-alignment.sh` | Task gate (`ok` / `prossegue`) |
| **D — Infra local** | Owner machine + Docker/Vault | `vault-init-dev.sh`, `vault-seed-*.sh`, `run-integration-tests.sh`, `api-live-smoke.sh`, `ensure-colima-docker.sh` | **`ok infra`** only |
| **E — Ops helpers** | Local optional | `observability-prometheus-token.sh`, `encode-env.sh`, `act-dev.sh` | Documented dev-only; no prod secrets |

Inventory detail: [`development.md`](../development.md#scripts).

## Change control

1. **Issue** with acceptance criteria (`.github/ISSUE_TEMPLATE/implementation.md`)
2. **Branch** from `sandbox`: `chore/<issue>-<slug>` or `docs/<issue>-<slug>`
3. **PR → `sandbox`** with `Refs #N` (delivery gate + CI)
4. **Promote → `main`** with `Closes #N` when releasing

Changes to Tier **A** or **B** scripts, `.github/workflows/`, or `.githooks/` require owner review via CODEOWNERS.

## CI parity

Local before push:

```bash
bash scripts/pre-push-check.sh
```

Includes delivery gate, secret scan, observability validation, Maven quality — same intent as `.github/workflows/maven.yml`.

## Analog (portfolio)

AIOS MCP tools use fixed catalogs and privilege ranks (`authorizeMcpTool`). VaultSpring scripts use **tiers + CODEOWNERS + branch protection** as the equivalent control plane.

## References

- [`SECURITY.md`](../../SECURITY.md) — posture table
- [`delivery-automation.md`](./delivery-automation.md) — event → workflow map
- [`local-runtime-authorization.md`](./local-runtime-authorization.md) — infra gate
- OWASP [CI/CD Security Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/CI_CD_Security_Cheat_Sheet.html)
- NIST [SP 800-218 SSDF](https://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-218.pdf)

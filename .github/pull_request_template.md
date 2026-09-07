## Summary

<!-- What changed and why. Link the issue below. -->

## PR type

- [ ] **Work → sandbox** — use `Refs #N` (required for CI `issue-link`)
- [ ] **Promote sandbox → main** — use `Closes #N`

Refs # / Closes #

## Change type

- [ ] feat
- [ ] fix
- [ ] docs
- [ ] refactor
- [ ] ci
- [ ] chore
- [ ] test

## Traceability

- [ ] GitHub issue exists and is linked above
- [ ] Branch uses semantic prefix (`feature/`, `fix/`, …) — ideally `feature/<issue>-<slug>`
- [ ] Kickoff comment on the issue (see `scripts/task-kickoff.sh`)
- [ ] Work PR targets **`sandbox`** (not `main` directly)

## Checklist

- [ ] Branch created from **`sandbox`** (or bootstrap documented)
- [ ] Target branch is **`sandbox`** (work) or **`main`** (promote only)
- [ ] `bash scripts/pre-push-check.sh` passed locally (delivery gate + CI parity)
- [ ] `./mvnw -B checkstyle:check test` passed locally (when Java/XML changed)
- [ ] `./mvnw -B verify -Pintegration-tests` when persistence/Flyway/API integration touched (Docker)
- [ ] Docs updated if this PR changes build, run, or architecture
- [ ] `CHANGELOG.md` `[Unreleased]` updated (if notable)
- [ ] No secrets, `.env`, or Vault unseal material
- [ ] No IDE attribution (`Co-authored-by: Cursor`, “Made with Cursor”) — author is **Kleilson Santos** ([`attribution.md`](docs/guides/attribution.md))

## Test plan

<!-- How to validate -->

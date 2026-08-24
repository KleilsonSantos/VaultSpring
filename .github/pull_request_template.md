## Summary

<!-- What changed and why. Link the issue: Closes #N -->

Closes #

## Change type

- [ ] feat
- [ ] fix
- [ ] docs
- [ ] refactor
- [ ] ci
- [ ] chore
- [ ] test

## Traceability

- [ ] GitHub issue exists and is linked above (`Closes #N`)
- [ ] Branch uses semantic prefix (`feature/`, `fix/`, …) — ideally `feature/<issue>-<slug>`
- [ ] Kickoff comment on the issue (see `scripts/task-kickoff.sh`)

## Checklist

- [ ] Branch created from `main`
- [ ] Target is `main`
- [ ] `./mvnw -B checkstyle:check test` passed locally (when Java/XML changed)
- [ ] `./mvnw -B verify -Pintegration-tests` when persistence/Flyway/API integration touched (Docker)
- [ ] Docs updated if this PR changes build, run, or architecture
- [ ] `CHANGELOG.md` `[Unreleased]` updated (if notable)
- [ ] No secrets, `.env`, or Vault unseal material

## Test plan

<!-- How to validate -->

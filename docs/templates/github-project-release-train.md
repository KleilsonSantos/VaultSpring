# Release train checklist — VaultSpring

Use as a **Project item** or PR checklist when promoting `sandbox` → `main` and tagging.

Replace `X.Y.Z` with the target version.

## Pre-promote

- [ ] All intended PRs merged to **`sandbox`**
- [ ] Open issues/PRs triaged (zero or explicitly deferred)
- [ ] `./mvnw -B checkstyle:check test` green on `sandbox`
- [ ] `bash scripts/pre-push-check.sh` green locally (or CI parity on last PR)

## Promote

- [ ] PR **`sandbox → main`** with `Closes #N` for shipped issues
- [ ] CI green (`quality`, `integration-tests`, **`issue-link`**)
- [ ] Merge promote PR

## Release commit

- [ ] `pom.xml` → `X.Y.Z` (no `-SNAPSHOT`)
- [ ] `CHANGELOG.md` — move `[Unreleased]` → `[X.Y.Z] - YYYY-MM-DD`
- [ ] `docs/guides/releases.md` — history row
- [ ] `bash scripts/check-semver-alignment.sh` OK
- [ ] PR **`chore: release vX.Y.Z`** → `main` — merge when CI green

## Tag and post-release

- [ ] Annotated tag `vX.Y.Z` on `main` HEAD
- [ ] `git push origin vX.Y.Z` — verify Release workflow
- [ ] PR bump to `(X.Y.(Z+1))-SNAPSHOT` on `main`
- [ ] PR **`main → sandbox`** sync
- [ ] `gh pr checks --watch` on release PRs
- [ ] Optional: `bash scripts/api-live-smoke.sh` after `ok infra`

## Status update (Project)

Post a short status update:

- **Summary:** what shipped in `vX.Y.Z`
- **Risk:** open Dependabot / AppSec gaps
- **Next:** target version and top 3 backlog items

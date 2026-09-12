# Releases and Tags

SemVer **`vMAJOR.MINOR.PATCH`** with **annotated tags** on `main`.

Version source of truth: `pom.xml` (`<version>` of the project artifact, not the Spring Boot parent).

## Policy

| Layer | Rule |
| ----- | ---- |
| Every commit | Conventional Commits message (no gitmoji) |
| Every release | Bump `pom.xml` version + `CHANGELOG.md` section + annotated tag |
| Gate | `scripts/check-semver-alignment.sh` — fails if `main` has **releaseable** commits after the last tag without a version bump |

Do **not** bump SemVer on every feature-branch commit. Aggregate at release time.

Commits that **do not** force a bump on their own: `chore`, `docs`, `ci`, `test`, `style`, `build`, `merge`.

Releaseable types: `feat`, `fix`, `perf`, `refactor` (when behavior-visible).

## SNAPSHOT vs release

During development, `pom.xml` may stay at `X.Y.Z-SNAPSHOT`. When cutting a release:

1. Set `<version>X.Y.Z</version>` (remove `-SNAPSHOT`)
2. Move `[Unreleased]` entries in `CHANGELOG.md` to `## [X.Y.Z] - YYYY-MM-DD`
3. Tag and push
4. Optionally bump to `(X.Y.(Z+1))-SNAPSHOT` on `main` for the next cycle

## Create a release

When the **SemVer gate** fails on `main` or `[Unreleased]` is ready, the agent (or maintainer) follows this — **not** a discretionary “later” step. Full event map: [`delivery-automation.md`](./delivery-automation.md).

```bash
git checkout main && git pull origin main

# 1. Align pom.xml + CHANGELOG [X.Y.Z]
bash scripts/check-semver-alignment.sh

# 2. Commit release prep (if not already merged)
git commit -m "chore: release vX.Y.Z"

# 3. Merge PR to main, then on main HEAD:
git tag -a vX.Y.Z -m "vX.Y.Z — short summary"

# 4. Push tag (triggers release workflow — do not duplicate with gh release create)
git push origin vX.Y.Z

# 5. Next dev cycle on main
# pom.xml → (X.Y.(Z+1))-SNAPSHOT, empty CHANGELOG [Unreleased]
```

## Local check

```bash
bash scripts/check-semver-alignment.sh
```

## History

| Tag | Description |
| --- | ----------- |
| `v0.1.8` | JWT fail-fast (prod/hom), GitHub Copilot agents and Projects guides, portfolio ecosystem hub |
| `v0.1.7` | User API RBAC (SEC-001): roles, `/users/me`, admin-only list/create |
| `v0.1.6` | Vault Database Secrets Engine (ADR-0005 Phase 1/2), delivery gate, doc audit |
| `v0.1.5` | Observability platform, sandbox branching (ADR-0004), JWT login |
| `v0.1.4` | API quality, Vault, SecurityFilterChain, CI hardening, technical docs hub |
| `v0.1.0` | Initial tagged baseline |

After tagging `vX.Y.Z`, bump `pom.xml` to `(X.Y.(Z+1))-SNAPSHOT` on `main` for the next cycle.

## Related

- [`git-workflow.md`](./git-workflow.md)
- [`CHANGELOG.md`](../../CHANGELOG.md)
- [portfolio-ecosystem.md](./portfolio-ecosystem.md) — AIOS origin links

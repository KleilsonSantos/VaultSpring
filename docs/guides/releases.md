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

```bash
git checkout main && git pull origin main

# 1. Align pom.xml + CHANGELOG [X.Y.Z]
bash scripts/check-semver-alignment.sh

# 2. Commit release prep (if not already merged)
git commit -m "chore: release vX.Y.Z"

# 3. Annotated tag
git tag -a vX.Y.Z -m "vX.Y.Z — short summary"

# 4. Push tag (triggers release workflow)
git push origin vX.Y.Z

# 5. GitHub Release notes
gh release create vX.Y.Z --title "vX.Y.Z — title" --notes "See CHANGELOG [X.Y.Z]."
```

## Local check

```bash
bash scripts/check-semver-alignment.sh
```

## History

| Tag | Description |
| --- | ----------- |
| `v0.1.0` | Initial tagged baseline |
| (pending) `v0.1.4` | API quality, Vault client, SecurityFilterChain, CI hardening — see `[Unreleased]` in CHANGELOG |

Current development version in `pom.xml`: `0.1.4-SNAPSHOT` (cut release when `[Unreleased]` is ready).

## Related

- [`git-workflow.md`](./git-workflow.md)
- [`CHANGELOG.md`](../../CHANGELOG.md)
- AIOS reference: [releases](https://github.com/KleilsonSantos/ai-operating-system/blob/main/docs/guides/releases.md)

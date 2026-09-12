# ADR-0004: Branch strategy with `sandbox` integration line

- **Status:** Accepted
- **Date:** 2026-09-06
- **Deciders:** Kleilson Santos
- **Supersedes:** informal “single branch `main` only” notes in early VaultSpring docs

## Context

VaultSpring adopted AIOS governance (issues, Conventional Commits, SemVer gate, delivery automation) but initially documented work branches merging **directly into `main`**. Other repositories in the same portfolio use a permanent **`sandbox`** integration branch: features integrate and pass CI on `sandbox` before promotion to `main`.

Maintaining two different promotion models causes confusion for contributors and agents moving between repos.

## Decision

Align VaultSpring with the **AIOS two-stage promotion** model (without gitmoji — see [`writing-style.md`](../guides/writing-style.md)):

```text
feature/* | fix/* | docs/* | chore/* | ci/* | …
              │
              ▼  PR #1 (Refs #N)
           sandbox          ← continuous integration
              │
              ▼  PR #2 (Closes #N)
            main             ← production line + annotated tags vX.Y.Z
```

### Rules

1. **No direct commits** to `main` or `sandbox` (after bootstrap).
2. **Kickoff** from up-to-date `sandbox`: `scripts/task-kickoff.sh`.
3. **PR #1** — work branch → `sandbox`: body must include `Refs #N` (or `#N`); CI job `issue-link` enforces on PRs targeting `sandbox`.
4. **PR #2** — `sandbox` → `main`: prefer `Closes #N` / `Fixes #N` (GitHub closing keywords apply on the default branch).
5. **Commits:** [Conventional Commits](https://www.conventionalcommits.org/) with optional scope — **no gitmoji**.
6. **SemVer gate** runs on push to `main` only (`scripts/check-semver-alignment.sh`).
7. **Dependabot version updates** target `sandbox`; security alerts remain in the GitHub Security tab.

### Bootstrap (one-time)

If `sandbox` does not exist on the remote:

```bash
bash scripts/bootstrap-sandbox.sh
```

Then enable branch protection on `sandbox` (required CI checks including `issue-link`) and keep `main` as default branch.

Historical work on `feature/*` cut from `main` before this ADR may merge to `sandbox` first, then promote with the rest of `sandbox`.

## Consequences

### Positive

- Consistent flow across portfolio repositories ([portfolio-ecosystem.md](../guides/portfolio-ecosystem.md)).
- Integration buffer on `sandbox` before production line `main`.
- Dependabot and feature work land on the same integration branch.

### Negative

- Two PRs per delivery slice (more process than single-PR trunk flow).
- Owner must configure branch protection on `sandbox`.

## References

- [`docs/guides/portfolio-ecosystem.md`](../guides/portfolio-ecosystem.md) — AIOS origin links (ADR-0002 and related guides)
- [`docs/guides/git-workflow.md`](../guides/git-workflow.md)
- [`docs/guides/delivery-automation.md`](../guides/delivery-automation.md)

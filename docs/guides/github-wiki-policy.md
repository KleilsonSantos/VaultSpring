# GitHub Wiki policy

## Decision

VaultSpring **does not use the GitHub Wiki as source of truth**.

Architecture, API, security, and runbooks live in **`docs/`** inside the repository ([ADR-0001](../adr/0001-record-architecture-decisions.md)): versioned, reviewable in PRs, aligned with `pom.xml`.

## Recommended repo setting

**Disable Wiki** (Settings → General → Features → Wiki) to prevent drift.

If Wiki stays enabled for navigation only, maintain a **single stub page** — copy from [`docs/wiki/Home.md`](../wiki/Home.md):

- Links to `docs/README.md`, `SECURITY.md`, `CONTRIBUTING.md`, `docs/api.md`
- Explicit warning: *do not edit deep content here*

## Do not put on Wiki

- Portfolio or private strategy notes
- Duplicate ADRs or architecture diagrams
- Credentials, `.env` examples with real values, or runbooks that bypass CI review
- Release steps that contradict [`releases.md`](./releases.md)

## Credibility principle

Visitors and recruiters should find **one authoritative path**: README → `docs/README.md` → guides/ADRs. A second wiki copy reduces trust when it goes stale.

## Related

- [`github-agents.md`](./github-agents.md)
- [`github-projects.md`](./github-projects.md)
- [`writing-style.md`](./writing-style.md)

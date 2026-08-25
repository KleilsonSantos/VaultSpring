# ADR-0001: Record architecture decisions in Git

## Status

Accepted

## Context

VaultSpring docs must stay aligned with `pom.xml` and source. Wiki-only or static PNG diagrams drift quickly.

## Decision

Store architecture documentation as Markdown in `docs/` with:

- **Mermaid** diagrams (render on GitHub, diffable in PRs)
- **ADRs** in `docs/adr/` for significant “why” decisions
- Cross-links from README, HELP, and `AGENTS.md`

## Consequences

- Positive: reviewable, versioned, AI- and human-readable
- Positive: diagrams update in the same PR as code
- Negative: authors must learn basic Mermaid syntax

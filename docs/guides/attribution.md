# Attribution and authorship (AIOS-aligned)

All delivery artifacts attribute **Kleilson Santos**, not IDE or AI tools.

## Canonical identity

| Field | Value |
| ----- | ----- |
| Author / Committer | **Kleilson Santos** |
| Email | **kleilson@icloud.com** |
| GitHub | [KleilsonSantos](https://github.com/KleilsonSantos) |

Same identity as `pom.xml` `<developers>` and [`git-workflow.md`](./git-workflow.md).

## Forbidden in commits and PRs

- `Co-authored-by: Cursor` / `cursoragent@cursor.com`
- `Co-authored-by: GitHub Copilot` or similar IDE trailers
- PR footer **“Made with Cursor”** (or any tool marketing line)
- Claiming Cursor, Copilot, or an agent as **author** in CHANGELOG or README

AI assistants **implement**; **you** author and merge. Hooks enforce this (`.githooks/commit-msg`).

## Allowed

- `AGENTS.md`, `.github/agents/`, `.cursor/rules/` — **tooling paths**, not authorship
- “AI agent layer” in docs meaning governance files for assistants
- Conventional Commits without co-author trailers

## Agent checklist

Before push or PR:

1. Commit author is the human identity above (default `git config` on maintainer machine)
2. No `Co-authored-by` IDE lines in commit message
3. PR body ends with test plan / `Closes #N` — **no** “Made with …”
4. CHANGELOG credits product changes, not the editor used to write them

## Related

- [`git-workflow.md`](./git-workflow.md)
- [`CONTRIBUTING.md`](../../CONTRIBUTING.md)
- AIOS reference: [git-workflow — merges and author](https://github.com/KleilsonSantos/ai-operating-system/blob/main/docs/guides/git-workflow.md)

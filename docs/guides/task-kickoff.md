# Task Kickoff (Canonical Flow)

Traceable delivery starts with a GitHub Issue, not with a branch name alone.

```text
Issue (GitHub) → In Progress → semantic branch from main → kickoff comment → PR → main
```

## Steps

1. **Open or pick an issue** with acceptance criteria ([Feature Request](../../.github/ISSUE_TEMPLATE/feature_request.md) or [Implementation](../../.github/ISSUE_TEMPLATE/implementation.md)).
2. **Assign / mark In Progress** on the project board when you use one.
3. **Create the branch** from up-to-date `main`:

   ```bash
   bash scripts/task-kickoff.sh <issue-number> <type>/<slug>
   ```

   Example:

   ```bash
   bash scripts/task-kickoff.sh 50 feature/50-problemdetail-openapi
   ```

4. **Implement** the slice; keep commits as Conventional Commits (`feat:`, `fix:`, …).
5. **Local QA** before push:

   ```bash
   ./mvnw -B checkstyle:check test
   ```

   Integration tests (Docker required):

   ```bash
   ./mvnw -B verify -Pintegration-tests
   ```

6. **Open PR** to `main` with `Closes #N` and a test plan.
7. **Merge** when required checks pass.

## Kickoff comment (automated)

`task-kickoff.sh` posts:

```markdown
Kickoff: branch `<type>/<slug>` created from `main` for this issue.
```

## Agent tooling + `gh`

AI agents in this repo follow [`attribution.md`](./attribution.md) — no IDE co-author or “Made with Cursor” on PRs.

The agent sandbox allowlists `github.com` (git) but not always `api.github.com` (REST used by `gh`). This repo ships [`.cursor/sandbox.json`](../../.cursor/sandbox.json) with `api.github.com` allowed.

In the IDE: **Settings → Agents → Auto Run → Network Access** → `sandbox.json + Defaults`.

## Traceability matrix

| Artifact | Must link to |
| -------- | ------------ |
| Branch | Issue number in name or kickoff comment |
| Commit | Conventional type; issue `#N` when useful |
| PR | `Closes #N`, CHANGELOG `[Unreleased]` if notable |
| Release tag | `CHANGELOG [X.Y.Z]` + `pom.xml` version |

## Related

- [`git-workflow.md`](./git-workflow.md)
- [`releases.md`](./releases.md)
- AIOS reference: [task-kickoff](https://github.com/KleilsonSantos/ai-operating-system/blob/main/docs/guides/task-kickoff.md)

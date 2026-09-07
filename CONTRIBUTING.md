# Contributing

Thanks for considering a contribution to **VaultSpring**.

Delivery governance follows patterns from [AI Operating System](https://github.com/KleilsonSantos/ai-operating-system), including the permanent **`sandbox`** integration branch ([ADR-0004](./docs/adr/0004-git-branching-strategy-sandbox.md)).

## Guides

| Doc | Purpose |
| --- | ------- |
| [`docs/README.md`](./docs/README.md) | Technical doc index (architecture, config, API, dev) |
| [`docs/guides/writing-style.md`](./docs/guides/writing-style.md) | Commits, issues, docs — no gitmoji (with references) |
| [`docs/guides/attribution.md`](./docs/guides/attribution.md) | Author identity — Kleilson Santos; no IDE trailers |
| [`docs/guides/delivery-automation.md`](./docs/guides/delivery-automation.md) | Push / PR / tag triggers (AIOS-aligned) |
| [`docs/guides/git-workflow.md`](./docs/guides/git-workflow.md) | Branches, PRs, commits |
| [`docs/guides/task-kickoff.md`](./docs/guides/task-kickoff.md) | Issue → branch → traceability |
| [`docs/guides/releases.md`](./docs/guides/releases.md) | SemVer, tags, CHANGELOG |

## Git flow

```text
GitHub Issue → feature/* | fix/* | … → PR → sandbox → PR → main → tag vX.Y.Z
```

Do **not** commit directly to `main` or `sandbox`. Do **not** open `feature/*` PRs straight to `main`.

Commits: [Conventional Commits](https://www.conventionalcommits.org/) — **no gitmoji**. Rationale: [`docs/guides/writing-style.md`](./docs/guides/writing-style.md).

### Kickoff (required for new work)

```bash
bash scripts/task-kickoff.sh <issue-number> <type>/<slug>
# e.g. bash scripts/task-kickoff.sh 50 feature/50-problemdetail-openapi
```

## Git hooks (recommended)

```bash
bash scripts/install-hooks.sh
# or: git config core.hooksPath .githooks
```

- `commit-msg`: Conventional Commits; blocks IDE co-authorship and “Made with Cursor” trailers
- `pre-commit`: `pom.xml` version bump when `pom.xml` is staged (release prep)

Do not use `--no-verify` in normal delivery.

## Quality gates

To merge into `sandbox` or `main`:

### Local

```bash
./mvnw -B checkstyle:check test
```

Optional integration tests (Docker):

```bash
./mvnw -B verify -Pintegration-tests
```

SemVer gate (before release merge):

```bash
bash scripts/check-semver-alignment.sh
```

### CI (GitHub Actions)

- Checkstyle + unit `verify` + JaCoCo (job `quality`)
- Integration tests with Testcontainers (job `integration-tests`, Docker on `ubuntu-latest`)
- Dependency Review on pull requests (job `dependency-review`)
- Dockerfile build smoke (job `docker-build`)
- CodeQL (`github/codeql-action@v4`) — **single path**: workflow in `.github/workflows/maven.yml`; disable GitHub **Default setup** for CodeQL in repo Settings to avoid duplicate scans
- SonarQube Cloud via **GitHub Automatic Analysis** — check `SonarCloud Code Analysis` on PRs; dashboard: [KleilsonSantos_VaultSpring](https://sonarcloud.io/project/overview?id=KleilsonSantos_VaultSpring). Do not run a Maven `sonar:sonar` job while Automatic Analysis is enabled.
- Release workflow on push of annotated tags `v*.*.*`

### GitHub settings (owner)

- Dependabot **alerts** on
- Secret scanning + push protection on
- Code scanning via the CodeQL job
- Branch protection on `main` and `sandbox`: required checks + no direct push
- `sandbox`: require `issue-link` on PRs from work branches

## How to contribute

1. Open an issue ([Implementation](./.github/ISSUE_TEMPLATE/implementation.md) or [Feature Request](./.github/ISSUE_TEMPLATE/feature_request.md))
2. Kickoff branch from `sandbox` (`scripts/task-kickoff.sh`; bootstrap: `scripts/bootstrap-sandbox.sh`)
3. Keep commits as `type: description`
4. **`bash scripts/pre-push-check.sh`** before push (delivery gate + Maven CI parity)
5. Open a PR with `Refs #N` / `Closes #N` and the template checklist
6. **`gh pr checks --watch`** after push — do not merge on red `issue-link`
7. Update `CHANGELOG.md` `[Unreleased]` for notable changes
8. Cut releases per [`docs/guides/releases.md`](./docs/guides/releases.md)

## Branch prefixes

`feature/` · `fix/` · `docs/` · `chore/` · `ci/` · `refactor/` · `test/` · `build/` · `perf/`

## Dependabot

Configured in [`.github/dependabot.yml`](./.github/dependabot.yml) for Maven, GitHub Actions, and Docker.

## Code of conduct

Be respectful. Security issues: see [SECURITY.md](./SECURITY.md).

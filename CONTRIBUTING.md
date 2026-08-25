# Contributing

Thanks for considering a contribution to **VaultSpring**.

Delivery governance follows patterns from [AI Operating System](https://github.com/KleilsonSantos/ai-operating-system), adapted for a single integration branch (`main`).

## Guides

| Doc | Purpose |
| --- | ------- |
| [`docs/guides/git-workflow.md`](./docs/guides/git-workflow.md) | Branches, PRs, commits |
| [`docs/guides/task-kickoff.md`](./docs/guides/task-kickoff.md) | Issue → branch → traceability |
| [`docs/guides/releases.md`](./docs/guides/releases.md) | SemVer, tags, CHANGELOG |

## Git flow

```text
GitHub Issue → feature/* | fix/* | … → PR → main → tag vX.Y.Z
```

Do **not** commit directly to `main`. VaultSpring does **not** use the AIOS `sandbox` branch.

Commits: [Conventional Commits](https://www.conventionalcommits.org/) — **no gitmoji**.

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

- `commit-msg`: Conventional Commits; blocks IDE co-authorship trailers
- `pre-commit`: `pom.xml` version bump when `pom.xml` is staged (release prep)

Do not use `--no-verify` in normal delivery.

## Quality gates

To merge into `main`:

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
- Branch protection on `main`: required checks + no direct push

## How to contribute

1. Open an issue ([Implementation](./.github/ISSUE_TEMPLATE/implementation.md) or [Feature Request](./.github/ISSUE_TEMPLATE/feature_request.md))
2. Kickoff branch from `main` (`scripts/task-kickoff.sh`)
3. Keep commits as `type: description`
4. Open a PR with `Closes #N` and the template checklist
5. Update `CHANGELOG.md` `[Unreleased]` for notable changes
6. Cut releases per [`docs/guides/releases.md`](./docs/guides/releases.md)

## Branch prefixes

`feature/` · `fix/` · `docs/` · `chore/` · `ci/` · `refactor/` · `test/` · `build/` · `perf/`

## Dependabot

Configured in [`.github/dependabot.yml`](./.github/dependabot.yml) for Maven, GitHub Actions, and Docker.

## Code of conduct

Be respectful. Security issues: see [SECURITY.md](./SECURITY.md).

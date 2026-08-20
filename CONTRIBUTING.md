# Contributing

Thanks for considering a contribution to **VaultSpring**.

## Git flow

```text
feature/* | fix/* | docs/* | chore/* | ci/*
              │
              ▼  PR
            main
```

Do not commit directly to `main`. Open a pull request.

This repository does **not** use the AIOS `sandbox` branch or gitmoji. Commits follow [Conventional Commits](https://www.conventionalcommits.org/).

## Quality gates

To merge into `main`:

### Local

```bash
./mvnw -B checkstyle:check test
```

Optional coverage:

```bash
./mvnw -B verify
```

### CI (GitHub Actions)

- Checkstyle
- Maven `verify` + JaCoCo
- Codecov upload (non-blocking)
- CodeQL (`github/codeql-action@v4`)
- SonarQube Cloud via **GitHub integration** (Automatic Analysis) — check `SonarCloud Code Analysis` on PRs; dashboard: [KleilsonSantos_VaultSpring](https://sonarcloud.io/project/overview?id=KleilsonSantos_VaultSpring). Do not run a Maven `sonar:sonar` job while Automatic Analysis is enabled (SonarCloud rejects duplicate CI analysis).
- Codecov for JaCoCo coverage in CI

### GitHub settings (owner)

- Dependabot **alerts** on
- Secret scanning + push protection on
- Code scanning via the CodeQL job
- Branch protection on `main`: required checks + no direct push

## How to contribute

1. Fork or create a branch from `main`
2. Use a semantic prefix: `feature/` · `fix/` · `docs/` · `chore/` · `ci/` · `refactor/` · `test/`
3. Keep commits as `type: description`
4. Open a PR using the template
5. Include docs in the same PR if build/run/architecture changes

## Dependabot

Configured in [`.github/dependabot.yml`](./.github/dependabot.yml) for Maven, GitHub Actions, and Docker. Review version bumps on `main` (this repo has a single long-lived branch).

## Code of conduct

Be respectful. Security issues: see [SECURITY.md](./SECURITY.md).

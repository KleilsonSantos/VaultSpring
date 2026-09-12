# GitHub Projects (VaultSpring Delivery)

Use **Projects v2** as the planning layer **on top of** issues and pull requests — not a parallel tracker.

Official reference: [Best practices for Projects](https://docs.github.com/en/issues/planning-and-tracking-with-projects/learning-about-projects/best-practices-for-projects).

## Project name

**VaultSpring Delivery** (user or org project linked to `KleilsonSantos/VaultSpring`).

## Schema (custom fields)

| Field | Type | Values |
| ----- | ---- | ------ |
| **Status** | Single select | Backlog · Ready · In progress · In review · Done · Blocked |
| **Priority** | Single select | P0 · P1 · P2 · P3 |
| **Area** | Single select | AppSec · API · Vault · Observability · CI/Delivery · Docs |
| **Release** | Text / Iteration | `v0.1.8`, `v0.1.9`, … |
| **Size** | Single select | S · M · L |

## Views

| View | Purpose |
| ---- | ------- |
| **Board** | Daily execution by `Status` |
| **Table** | Triage — sort by `Priority`, filter by `Area` |
| **Roadmap** | Release communication — `Release` + dates |

## Built-in automations (enable first)

- New issue/PR in linked repo → add to project
- Issue closed → `Status = Done`
- PR merged → `Status = Done`
- Archive `Done` items after 30 days

Advanced automation (GraphQL / Actions) only when built-in rules are insufficient — see [Automating Projects](https://docs.github.com/en/issues/planning-and-tracking-with-projects/automating-your-project/automating-projects-using-actions).

## SDLC mapping

```text
Issue (acceptance criteria)
  → branch from sandbox
  → PR → sandbox          Status: In review
  → merge sandbox         Status: Done (feature)
  → PR sandbox → main     Release: vX.Y.Z
  → tag vX.Y.Z              Roadmap: shipped
```

Every card must link to an **Issue** or **PR**. No orphan tasks.

## Release train template

Copy checklist: [`../templates/github-project-release-train.md`](../templates/github-project-release-train.md).

Use **Project status updates** (weekly or per release) for risks, blockers, and target date — see [GitHub blog — status updates](https://github.blog/developer-skills/github/how-were-using-github-projects-to-standardize-our-workflows-and-stay-aligned/).

## Setup checklist (manual, ~1 h)

1. Create project **VaultSpring Delivery**
2. Link repository `KleilsonSantos/VaultSpring`
3. Add custom fields (table above)
4. Create Board, Table, Roadmap views
5. Enable built-in automations
6. Add current cycle: promote `sandbox→main`, next SemVer, Dependabot triage
7. Optional: save as **project template** for future releases

## Related

- [`delivery-automation.md`](./delivery-automation.md)
- [`git-workflow.md`](./git-workflow.md)
- [`releases.md`](./releases.md)
- [`github-agents.md`](./github-agents.md)

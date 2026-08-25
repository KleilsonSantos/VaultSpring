# Diagrams (Mermaid)

All diagrams are **text in Markdown** — editable in PRs, rendered by GitHub.

| Diagram | File | Purpose |
| ------- | ---- | ------- |
| C4 Context | [architecture.md](../architecture.md#c4-context-level-1) | System vs users and externals |
| C4 Container | [architecture.md](../architecture.md#c4-container-level-2) | App, DB, Vault, CI |
| Request flow | [api.md](../api.md#create-user-sequence) | POST `/api/v1/users` |
| Profile picker | [configuration.md](../configuration.md#profile-selection-flow) | Which profile to use |
| Local dev | [development.md](../development.md#local-development-flow) | Clone → run |
| CI pipeline | [development.md](../development.md#ci-pipeline-overview) | GitHub Actions jobs |
| Delivery | [guides/git-workflow.md](../guides/git-workflow.md) | Issue → PR → release |

Do not add binary PNG/SVG unless generated from these sources in CI.

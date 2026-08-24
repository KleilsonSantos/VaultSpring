---
name: Implementation
about: Traceable feature or improvement with delivery checklist
title: '[feat] '
labels: enhancement
---

## Summary

<!-- One paragraph: what will be delivered -->

## Motivation

<!-- Problem / opportunity; link related issues -->

## Acceptance criteria

- [ ]
- [ ]

## Delivery traceability

| Field | Value |
| ----- | ----- |
| Target branch | `main` |
| Work branch | `feature/<issue>-<slug>` |
| CHANGELOG | `[Unreleased]` when merged |
| Release | patch / minor / none |

## Test plan

- [ ] `./mvnw -B checkstyle:check test`
- [ ] `./mvnw -B verify -Pintegration-tests` (if persistence/API touched)
- [ ] Manual steps (if any):

## Out of scope

<!-- Explicit non-goals -->

## References

- [Git workflow](../../docs/guides/git-workflow.md)
- [Task kickoff](../../docs/guides/task-kickoff.md)

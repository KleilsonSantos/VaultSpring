#!/usr/bin/env bash
# Create a semantic branch from main and comment on the GitHub issue.
#
# Usage:
#   bash scripts/task-kickoff.sh <issue-number> <branch-name>
#
# Example:
#   bash scripts/task-kickoff.sh 50 feature/50-problemdetail-openapi

set -euo pipefail

ISSUE="${1:?issue number required}"
BRANCH="${2:?branch name required (e.g. feature/50-slug)}"
REPO="${GITHUB_REPOSITORY:-KleilsonSantos/VaultSpring}"

git checkout main
git pull origin main
git checkout -b "$BRANCH"

if command -v gh >/dev/null 2>&1; then
  gh issue comment "$ISSUE" --repo "$REPO" \
    --body "Kickoff: branch \`${BRANCH}\` created from \`main\` for this issue."
  echo "Comment posted on issue #${ISSUE}"
else
  echo "gh not found — create kickoff comment on issue #${ISSUE} manually"
fi

echo "Ready on branch ${BRANCH}"

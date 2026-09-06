#!/usr/bin/env bash
# Create a semantic branch from sandbox and comment on the GitHub issue.
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
BASE="${KICKOFF_BASE_BRANCH:-sandbox}"

git fetch origin "$BASE" 2>/dev/null || true
if git show-ref --verify --quiet "refs/remotes/origin/${BASE}"; then
  git checkout "$BASE"
  git pull origin "$BASE"
else
  echo "Remote origin/${BASE} not found. Bootstrap with: bash scripts/bootstrap-sandbox.sh"
  echo "Falling back to main for first-time setup only."
  git checkout main
  git pull origin main
  BASE="main"
fi

git checkout -b "$BRANCH"

if command -v gh >/dev/null 2>&1; then
  gh issue comment "$ISSUE" --repo "$REPO" \
    --body "Kickoff: branch \`${BRANCH}\` created from \`${BASE}\` for this issue. PR target: \`sandbox\` (Refs #${ISSUE})."
  echo "Comment posted on issue #${ISSUE}"
else
  echo "gh not found — create kickoff comment on issue #${ISSUE} manually"
fi

echo "Ready on branch ${BRANCH} (base: ${BASE}). Open PR → sandbox with Refs #${ISSUE}."

#!/usr/bin/env bash
# One-time bootstrap: create remote sandbox from main if missing.
#
# Usage:
#   bash scripts/bootstrap-sandbox.sh
#
# After push, configure branch protection on sandbox (required CI + issue-link).

set -euo pipefail

git fetch origin

if git ls-remote --exit-code --heads origin sandbox >/dev/null 2>&1; then
  echo "sandbox already exists on origin — nothing to do."
  echo "Ensure local tracking: git checkout sandbox && git pull origin sandbox"
  exit 0
fi

echo "Creating origin/sandbox from origin/main..."
git push origin origin/main:refs/heads/sandbox

echo "Done. Next:"
echo "  1. GitHub → Settings → Branches → protect sandbox (required checks: quality, issue-link, …)"
echo "  2. git checkout -b sandbox origin/sandbox  # if needed locally"
echo "  3. New work: bash scripts/task-kickoff.sh <issue> feature/<issue>-<slug>"

#!/usr/bin/env bash
set -euo pipefail

# 📁 Point Git at versioned hooks (does not copy into .git/hooks)
git config core.hooksPath .githooks
chmod +x .githooks/pre-commit .githooks/commit-msg \
  scripts/check-version-alignment.sh scripts/check-semver-alignment.sh scripts/task-kickoff.sh

echo "Git hooks path set to .githooks"

#!/usr/bin/env bash
set -euo pipefail

# 📁 Point Git at versioned hooks (does not copy into .git/hooks)
git config core.hooksPath .githooks
chmod +x .githooks/pre-commit scripts/check-version-alignment.sh

echo "Git hooks path set to .githooks"

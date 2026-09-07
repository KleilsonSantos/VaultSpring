#!/usr/bin/env bash
# Local delivery gate — mirrors CI before commit/push (quality + PR metadata + configs).
#
# Usage:
#   bash scripts/pre-push-check.sh
#
# Optional:
#   PR_BASE=main bash scripts/pre-push-check.sh     # promote/release PR validation
#   RUN_INTEGRATION=1 bash scripts/pre-push-check.sh
#
# Canonical order: docs/guides/local-runtime-authorization.md (step 4b)

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

echo "==> PR delivery gate (issue-link parity — diagnose locally)"
bash scripts/check-pr-delivery-gate.sh

echo "==> observability config validation (CI parity)"
bash scripts/validate-observability-config.sh

echo "==> checkstyle + unit verify (CI quality job)"
./mvnw -B checkstyle:check verify --no-transfer-progress

CURRENT="$(git branch --show-current)"
if [[ "$CURRENT" == "main" || "${PR_BASE:-}" == "main" ]]; then
  echo "==> semver alignment (main)"
  bash scripts/check-semver-alignment.sh
fi

if [[ "${RUN_INTEGRATION:-}" == "1" ]]; then
  echo "==> integration tests (Testcontainers)"
  bash scripts/run-integration-tests.sh
fi

echo "pre-push-check: OK"

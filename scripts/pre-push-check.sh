#!/usr/bin/env bash
# Local gate mirroring CI job "quality" (maven.yml) — run before commit/push when Java changed.
#
# Usage:
#   bash scripts/pre-push-check.sh
#
# Optional integration (Docker):
#   RUN_INTEGRATION=1 bash scripts/pre-push-check.sh

set -euo pipefail

echo "==> checkstyle + unit verify (same as CI quality job)"
./mvnw -B checkstyle:check verify --no-transfer-progress

if [[ "${RUN_INTEGRATION:-}" == "1" ]]; then
  echo "==> integration tests (Testcontainers)"
  bash scripts/run-integration-tests.sh
fi

echo "pre-push-check: OK"

#!/usr/bin/env bash
# Smoke tests for check-pr-delivery-gate.sh (no network when GH_TOKEN unset).

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

run_ok() {
  echo "SELFTEST OK: $*"
  "$@"
}

run_fail() {
  echo "SELFTEST expect FAIL: $*"
  if "$@"; then
    echo "SELFTEST ERROR: expected failure" >&2
    exit 1
  fi
}

run_ok env PR_BASE=sandbox PR_HEAD=main bash scripts/check-pr-delivery-gate.sh
run_ok env PR_BASE=main PR_HEAD=sandbox bash scripts/check-pr-delivery-gate.sh
run_ok env PR_BASE=sandbox PR_HEAD=feature/83-observability-platform bash scripts/check-pr-delivery-gate.sh
run_ok env PR_BASE=sandbox PR_TITLE="sync" PR_BODY="Refs #83" PR_HEAD=chore/sync-sandbox bash scripts/check-pr-delivery-gate.sh
run_fail env PR_BASE=sandbox PR_HEAD=chore/sync-without-issue bash scripts/check-pr-delivery-gate.sh

echo "check-pr-delivery-gate-selftest: OK"

#!/usr/bin/env bash
# Run Failsafe integration tests (Testcontainers). Requires Docker (Colima or Docker Desktop).
#
# Usage:
#   bash scripts/run-integration-tests.sh
#
# Colima (macOS): ensures DOCKER_HOST points at the Colima socket when present.

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
# shellcheck source=ensure-colima-docker.sh
source "${ROOT}/scripts/ensure-colima-docker.sh"

if ! docker info >/dev/null 2>&1; then
  echo "Docker is not available. Start Colima (colima start) or Docker Desktop, then retry."
  exit 1
fi

exec ./mvnw -B verify -Pintegration-tests "$@"

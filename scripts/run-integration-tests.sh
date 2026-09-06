#!/usr/bin/env bash
# Run Failsafe integration tests (Testcontainers). Requires Docker (Colima or Docker Desktop).
#
# Usage:
#   bash scripts/run-integration-tests.sh
#
# Colima (macOS): ensures DOCKER_HOST points at the Colima socket when present.

set -euo pipefail

if [ -S "${HOME}/.colima/default/docker.sock" ]; then
  export DOCKER_HOST="unix://${HOME}/.colima/default/docker.sock"
  export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE="/var/run/docker.sock"
fi

if command -v docker >/dev/null 2>&1; then
  :
elif [ -x "/usr/local/Cellar/docker/29.5.3/bin/docker" ]; then
  export PATH="/usr/local/Cellar/docker/29.5.3/bin:${PATH}"
fi

if ! docker info >/dev/null 2>&1; then
  echo "Docker is not available. Start Colima (colima start) or Docker Desktop, then retry."
  exit 1
fi

exec ./mvnw -B verify -Pintegration-tests "$@"

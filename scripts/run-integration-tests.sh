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

if ! command -v docker >/dev/null 2>&1; then
  _docker_bin="$(find /usr/local/Cellar/docker /opt/homebrew/Cellar/docker -name docker -type f 2>/dev/null | sort -V | tail -1)"
  if [ -n "${_docker_bin}" ]; then
    _docker_dir="$(dirname "${_docker_bin}")"
    export PATH="${_docker_dir}:${PATH}"
    unset _docker_dir
  fi
  unset _docker_bin
fi

if ! docker info >/dev/null 2>&1; then
  echo "Docker is not available. Start Colima (colima start) or Docker Desktop, then retry."
  exit 1
fi

exec ./mvnw -B verify -Pintegration-tests "$@"

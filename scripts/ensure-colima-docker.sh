#!/usr/bin/env bash
# Configure Docker CLI for Colima on macOS (shared by observability and IT scripts).
#
# Usage:
#   source scripts/ensure-colima-docker.sh

if [ -S "${HOME}/.colima/default/docker.sock" ]; then
  export DOCKER_HOST="unix://${HOME}/.colima/default/docker.sock"
  export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE="/var/run/docker.sock"
fi

if ! command -v docker >/dev/null 2>&1; then
  # find exits 1 when a Cellar path is missing; do not fail callers using set -o pipefail.
  _docker_bin="$(find /usr/local/Cellar/docker /opt/homebrew/Cellar/docker -path '*/bin/docker' -type f 2>/dev/null | sort -V | tail -1 || true)"
  if [ -n "${_docker_bin}" ]; then
    _docker_dir="$(dirname "${_docker_bin}")"
    export PATH="${_docker_dir}:${PATH}"
    unset _docker_dir
  fi
  unset _docker_bin
fi

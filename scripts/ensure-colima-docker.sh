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
  _docker_bin="$(find /usr/local/Cellar/docker /opt/homebrew/Cellar/docker -name docker -type f 2>/dev/null | sort -V | tail -1)"
  if [ -n "${_docker_bin}" ]; then
    export PATH="$(dirname "${_docker_bin}"):${PATH}"
  fi
  unset _docker_bin
fi

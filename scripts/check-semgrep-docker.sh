#!/usr/bin/env bash
# Local SAST (Semgrep via Docker) — advisory pre-push pass, fail-soft.
# Uses a pinned, non-root image with the source tree mounted read-only.
#
# Usage:
#   bash scripts/check-semgrep-docker.sh
#
# Requires Docker Desktop running. Skips if Docker is unavailable and never
# blocks the pre-push gate.

set -uo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT" || exit
export PATH="$HOME/.docker/bin:$PATH"

if ! command -v docker >/dev/null 2>&1; then
  echo "check-semgrep-docker: SKIP — docker CLI not found (advisory step, not required)"
  exit 0
fi

if ! docker info >/dev/null 2>&1; then
  echo "check-semgrep-docker: SKIP — Docker daemon not running (advisory step, not required)"
  exit 0
fi

echo "==> Semgrep SAST via Docker (advisory, fail-soft)"

docker run --rm --volume "$ROOT:/src:ro" --workdir /src \
  semgrep/semgrep:1.184.0-nonroot@sha256:f62960e8bc7c3d6bf2f5acebdb8e25add95e1efbc93a479c3ed3d5366619ac24 \
  semgrep scan --config p/owasp-top-ten --quiet /src/src/main/java || true

echo "check-semgrep-docker: done (findings above, if any — does not block push)"
exit 0

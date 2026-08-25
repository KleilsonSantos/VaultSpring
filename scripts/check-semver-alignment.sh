#!/usr/bin/env bash
# SemVer anti-drift gate for VaultSpring (pom.xml + CHANGELOG).
# Fails if releaseable commits exist after the last v* tag without a pom version bump.
#
# Usage:
#   bash scripts/check-semver-alignment.sh
#   bash scripts/check-semver-alignment.sh HEAD

set -euo pipefail

HEAD_REF="${1:-HEAD}"
ROOT="$(git rev-parse --show-toplevel)"
cd "$ROOT"

POM_FILE="$ROOT/pom.xml"
if [[ ! -f "$POM_FILE" ]]; then
  echo "semver-align: pom.xml missing — skip"
  exit 0
fi

POM_VERSION="$(awk '
  /<parent>/ { in_parent=1 }
  /<\/parent>/ { in_parent=0 }
  /<version>/ && !in_parent {
    gsub(/.*<version>/, "", $0);
    gsub(/<\/version>.*/, "", $0);
    print $0;
    exit;
  }
' "$POM_FILE")"

POM_VERSION_CLEAN="$(echo "$POM_VERSION" | sed -E 's/-SNAPSHOT//')"

LAST_TAG="$(git tag -l 'v*.*.*' --sort=-v:refname | head -n 1 || true)"
if [[ -z "$LAST_TAG" ]]; then
  echo "semver-align: no v* tag — skip (bootstrap)"
  exit 0
fi

TAG_VERSION="${LAST_TAG#v}"
TAG_VERSION_CLEAN="$(echo "$TAG_VERSION" | sed -E 's/-SNAPSHOT//')"

echo "semver-align: pom.xml=$POM_VERSION  last_tag=$LAST_TAG"

version_gt() {
  python3 - "$1" "$2" <<'PY'
import sys

def parse(v):
    return tuple(int(x) for x in v.split(".")[:3])

a, b = parse(sys.argv[1]), parse(sys.argv[2])
sys.exit(0 if a > b else 1)
PY
}

RANGE="${LAST_TAG}..${HEAD_REF}"
if ! git rev-parse --verify "$LAST_TAG" >/dev/null 2>&1; then
  echo "semver-align: invalid tag $LAST_TAG"
  exit 1
fi

mapfile -t SUBJECTS < <(git log --format='%s' "$RANGE" 2>/dev/null || true)
if [[ ${#SUBJECTS[@]} -eq 0 ]]; then
  echo "semver-align: no commits after $LAST_TAG — OK"
  exit 0
fi

RELEASEABLE=0
for subject in "${SUBJECTS[@]}"; do
  [[ -z "$subject" ]] && continue
  if [[ "$subject" =~ ^merge: ]]; then
    continue
  fi
  if [[ "$subject" =~ ^(feat|fix|perf)(\(|:|\!) ]]; then
    RELEASEABLE=1
    break
  fi
  if [[ "$subject" =~ ^refactor(\(|:|\!) ]]; then
    RELEASEABLE=1
    break
  fi
done

if [[ "$RELEASEABLE" -eq 0 ]]; then
  echo "semver-align: no releaseable commits after $LAST_TAG — OK"
  exit 0
fi

# SNAPSHOT = dev cycle on main; releaseable commits are expected until a release PR lands.
if [[ "$POM_VERSION" == *-SNAPSHOT ]]; then
  echo "semver-align: SNAPSHOT $POM_VERSION with releaseable commits after $LAST_TAG — OK (cut release via PR)"
  exit 0
fi

# Non-SNAPSHOT on main: version must be ahead of last tag and CHANGELOG must match.
if version_gt "$POM_VERSION_CLEAN" "$TAG_VERSION_CLEAN"; then
  if ! grep -q "## \\[$POM_VERSION_CLEAN\\]" "$ROOT/CHANGELOG.md" 2>/dev/null; then
    echo "semver-align: pom.xml is $POM_VERSION_CLEAN but CHANGELOG.md lacks ## [$POM_VERSION_CLEAN]"
    exit 1
  fi
  echo "semver-align: version bumped and CHANGELOG section present — OK"
  exit 0
fi

echo "semver-align: releaseable commits after $LAST_TAG but pom.xml still at $POM_VERSION (non-SNAPSHOT)"
echo "semver-align: bump pom.xml and add CHANGELOG [X.Y.Z] before merging to main"
exit 1

#!/usr/bin/env bash
# Local GitGuardian parity — catch secret-scan failures before push (ggshield + .gitguardian.yml).
#
# Usage:
#   bash scripts/check-secrets.sh
#   GGSHIELD_CMD="/opt/homebrew/bin/ggshield" bash scripts/check-secrets.sh
#   Allowed GGSHIELD_CMD values: absolute path to ggshield, or whitelisted: pipx run ggshield | python3 -m ggshield
#
# Install ggshield (pick one):
#   brew install gitguardian/tap/ggshield
#   pipx install ggshield
#
# See: docs/development.md#secret-scanning

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

CONFIG="${ROOT}/.gitguardian.yml"

if [[ ! -f "$CONFIG" ]]; then
  echo "check-secrets: FAIL — missing ${CONFIG#${ROOT}/}" >&2
  exit 1
fi

validate_config_yaml() {
  if ruby -ryaml -e "YAML.load_file('${CONFIG}')" >/dev/null 2>&1; then
    return 0
  fi
  if python3 -c "import yaml; yaml.safe_load(open('${CONFIG}'))" >/dev/null 2>&1; then
    return 0
  fi
  return 1
}

if ! validate_config_yaml; then
  echo "check-secrets: FAIL — invalid YAML in .gitguardian.yml (install Ruby psych or PyYAML)" >&2
  exit 1
fi
echo "check-secrets: OK — .gitguardian.yml syntax valid"

run_ggshield() {
  if [[ -n "${GGSHIELD_CMD:-}" ]]; then
    case "$GGSHIELD_CMD" in
      pipx\ run\ ggshield)
        pipx run ggshield "$@"
        return $?
        ;;
      python3\ -m\ ggshield)
        python3 -m ggshield "$@"
        return $?
        ;;
      ggshield)
        ggshield "$@"
        return $?
        ;;
      /*)
        if [[ -x "$GGSHIELD_CMD" ]]; then
          "$GGSHIELD_CMD" "$@"
          return $?
        fi
        echo "check-secrets: FAIL — GGSHIELD_CMD not executable: $GGSHIELD_CMD" >&2
        return 1
        ;;
      *)
        echo "check-secrets: FAIL — unsupported GGSHIELD_CMD (use absolute path or whitelisted alias)" >&2
        return 1
        ;;
    esac
  fi
  if command -v ggshield >/dev/null 2>&1; then
    ggshield "$@"
    return $?
  fi
  if command -v pipx >/dev/null 2>&1; then
    pipx run ggshield "$@"
    return $?
  fi
  if python3 -c "import ggshield" >/dev/null 2>&1; then
    python3 -m ggshield "$@"
    return $?
  fi
  return 127
}

if ! run_ggshield --version >/dev/null 2>&1; then
  cat >&2 <<'EOF'
check-secrets: FAIL — ggshield not installed (GitGuardian local parity).

Install (pick one):
  brew trust gitguardian/tap && brew install gitguardian/tap/ggshield
  python3 -m venv ~/.local/ggshield-venv && ~/.local/ggshield-venv/bin/pip install ggshield
  ln -sf ~/.local/ggshield-venv/bin/ggshield ~/.local/bin/ggshield  # add ~/.local/bin to PATH

Then re-run: bash scripts/pre-push-check.sh
Docs: docs/development.md#secret-scanning
EOF
  exit 1
fi

scan_range() {
  local base="$1"
  local head="$2"
  if [[ "$base" == "$head" ]]; then
    return 1
  fi
  echo "check-secrets: scanning commit range ${base}..${head}"
  if run_ggshield --config-path "$CONFIG" secret scan commit-range "${base}..${head}"; then
    return 0
  fi
  local rc=$?
  echo "check-secrets: FAIL — ggshield scan failed (exit ${rc}). Run: ggshield auth login" >&2
  exit "$rc"
}

# Prefer commits not yet on upstream (pre-push parity).
if git rev-parse --abbrev-ref '@{upstream}' >/dev/null 2>&1; then
  REMOTE_SHA=$(git rev-parse "@{upstream}" 2>/dev/null || true)
  HEAD_SHA=$(git rev-parse HEAD)
  if [[ -n "$REMOTE_SHA" ]] && scan_range "$REMOTE_SHA" "$HEAD_SHA"; then
    echo "check-secrets: OK"
    exit 0
  fi
fi

# Feature branches: compare to integration branch.
for BASE_REF in "origin/sandbox" "origin/main"; do
  if git rev-parse --verify "$BASE_REF" >/dev/null 2>&1; then
    MERGE_BASE=$(git merge-base HEAD "$BASE_REF")
    HEAD_SHA=$(git rev-parse HEAD)
    if scan_range "$MERGE_BASE" "$HEAD_SHA"; then
      echo "check-secrets: OK"
      exit 0
    fi
  fi
done

# Staged changes (pre-commit parity).
if ! git diff --cached --quiet; then
  echo "check-secrets: scanning staged changes (pre-commit mode)"
  if run_ggshield --config-path "$CONFIG" secret scan pre-commit; then
    echo "check-secrets: OK"
    exit 0
  fi
  echo "check-secrets: FAIL — ggshield pre-commit scan failed. Run: ggshield auth login" >&2
  exit 1
fi

echo "check-secrets: OK — nothing to scan (no unpushed commits or staged changes)"
exit 0

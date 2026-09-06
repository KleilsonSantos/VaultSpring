#!/usr/bin/env bash
# Validate observability configuration files (syntax only; no live stack required).

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
# shellcheck source=ensure-colima-docker.sh
source "${ROOT}/scripts/ensure-colima-docker.sh"
OBS="${ROOT}/docker/observability"

echo "Validating observability configs under ${OBS}..."

validate_yaml() {
  local file="$1"
  if ruby -ryaml -e "YAML.load_file('$file')" >/dev/null 2>&1; then
    echo "OK YAML  ${file#${ROOT}/}"
    return 0
  fi
  if python3 -c "import yaml; yaml.safe_load(open('$file'))" >/dev/null 2>&1; then
    echo "OK YAML  ${file#${ROOT}/}"
    return 0
  fi
  echo "FAIL YAML ${file#${ROOT}/} (install Ruby psych or PyYAML)" >&2
  return 1
}

validate_json() {
  local file="$1"
  python3 -c "import json; json.load(open('$file'))"
  echo "OK JSON  ${file#${ROOT}/}"
}

while IFS= read -r -d '' file; do
  validate_yaml "$file"
done < <(find "$OBS" -type f \( -name '*.yml' -o -name '*.yaml' \) -print0)

while IFS= read -r -d '' file; do
  validate_json "$file"
done < <(find "$OBS/grafana/dashboards" -type f -name '*.json' -print0)

COMPOSE="${OBS}/docker-compose.observability.yml"
if command -v docker >/dev/null 2>&1 && docker info >/dev/null 2>&1; then
  docker compose -f "$COMPOSE" config >/dev/null
  echo "OK COMPOSE ${COMPOSE#${ROOT}/}"
else
  echo "WARN COMPOSE ${COMPOSE#${ROOT}/} skipped (Colima/Docker unavailable — colima start + brew install docker)"
fi

echo "Observability config validation passed."

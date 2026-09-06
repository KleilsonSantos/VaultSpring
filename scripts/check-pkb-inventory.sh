#!/usr/bin/env bash
# PKB inventory drift check — index.yaml vs by-domain assets.
# Usage: bash scripts/check-pkb-inventory.sh

set -euo pipefail

ROOT="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
cd "$ROOT"

PKB_ROOT="$ROOT/docs/prompts"
INDEX="$PKB_ROOT/index.yaml"

if [[ ! -f "$INDEX" ]]; then
  echo "pkb-inventory: missing $INDEX"
  exit 1
fi

ERRORS=0

check_file() {
  local rel="$1"
  local full="$PKB_ROOT/$rel"
  if [[ ! -f "$full" ]]; then
    echo "pkb-inventory: index entry missing file: $rel"
    ERRORS=$((ERRORS + 1))
    return
  fi

  local basename
  basename="$(basename "$full")"
  local version_from_name
  version_from_name="$(echo "$basename" | sed -nE 's/.*\.v([0-9]+)\.md/\1/p')"
  if [[ -z "$version_from_name" ]]; then
    echo "pkb-inventory: filename must match *.vN.md: $rel"
    ERRORS=$((ERRORS + 1))
  fi

  local id domain version
  id="$(awk '/^id: / { print $2; exit }' "$full")"
  domain="$(awk '/^domain: / { print $2; exit }' "$full")"
  version="$(awk '/^version: / { print $2; exit }' "$full")"

  if [[ -z "$id" || -z "$domain" || -z "$version" ]]; then
    echo "pkb-inventory: incomplete frontmatter in $rel"
    ERRORS=$((ERRORS + 1))
    return
  fi

  if [[ "$version" != "$version_from_name" ]]; then
    echo "pkb-inventory: version mismatch in $rel (frontmatter=$version file.v$version_from_name)"
    ERRORS=$((ERRORS + 1))
  fi

  local expected_domain
  expected_domain="$(dirname "$rel")"
  expected_domain="${expected_domain#by-domain/}"
  if [[ "$domain" != "$expected_domain" && "$domain" != "templates" ]]; then
    echo "pkb-inventory: domain '$domain' vs path '$rel' (expected $expected_domain)"
    ERRORS=$((ERRORS + 1))
  fi
}

while IFS= read -r line; do
  if [[ "$line" =~ path:\ (.+) ]]; then
    rel="${BASH_REMATCH[1]}"
    check_file "$rel"
  fi
done < "$INDEX"

while IFS= read -r -d '' asset; do
  rel="${asset#$PKB_ROOT/}"
  if [[ "$rel" == templates/* ]] || [[ "$rel" == archived/* ]]; then
    continue
  fi
  if ! grep -q "path: $rel" "$INDEX"; then
    echo "pkb-inventory: orphan asset (not in index.yaml): $rel"
    ERRORS=$((ERRORS + 1))
  fi
done < <(find "$PKB_ROOT/by-domain" -name '*.v*.md' -print0 2>/dev/null)

if [[ "$ERRORS" -gt 0 ]]; then
  echo "pkb-inventory: FAIL ($ERRORS problem(s))"
  exit 1
fi

COUNT="$(grep -c '^  - id:' "$INDEX" || true)"
echo "pkb-inventory: OK ($COUNT catalog entries)"

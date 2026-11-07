#!/usr/bin/env bash
# Local AI review (Claude Code) — advisory pre-push pass on the diff vs sandbox/main.
# Fail-soft by design: never blocks the push. CI + the PR-level review remain the
# real gates; this is a fast, local first pass before the diff leaves the machine.
#
# Usage:
#   bash scripts/check-ai-review.sh
#
# Requires: claude CLI on PATH, already authenticated.
# Skips silently (exit 0) if claude is missing, there is no base ref, or there is
# nothing to review — this step never fails the gate.

set -uo pipefail # no -e: a broken review step must never abort the push

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

if ! command -v claude >/dev/null 2>&1; then
  echo "check-ai-review: SKIP — claude CLI not found (advisory step, not required)"
  exit 0
fi

BASE_REF=""
for CANDIDATE in "origin/sandbox" "origin/main"; do
  if git rev-parse --verify "$CANDIDATE" >/dev/null 2>&1; then
    BASE_REF="$CANDIDATE"
    break
  fi
done

if [[ -z "$BASE_REF" ]]; then
  echo "check-ai-review: SKIP — no sandbox/main ref to diff against"
  exit 0
fi

MERGE_BASE="$(git merge-base HEAD "$BASE_REF" 2>/dev/null || true)"
if [[ -z "$MERGE_BASE" ]]; then
  echo "check-ai-review: SKIP — no merge-base with $BASE_REF"
  exit 0
fi

DIFF="$(git diff "${MERGE_BASE}...HEAD" -- '*.java')"
if [[ -z "$DIFF" ]]; then
  echo "check-ai-review: OK — no Java changes to review"
  exit 0
fi

echo "==> AI review (advisory, fail-soft) — ${MERGE_BASE:0:8}..HEAD"

PROMPT=$(
  cat <<EOF
Review the following diff from VaultSpring (a Spring Boot secrets-management
service). Follow the checklist in .github/agents/code-reviewer.agent.md.
Report ONLY high-confidence CRITICAL issues (security, broken logic, leaked
secrets) that MUST be fixed before this reaches the remote, each prefixed
with "CRITICAL:". If there are none, reply with exactly
"NO CRITICAL FINDINGS". Be terse — no praise, no restating the diff.

Diff:
${DIFF}
EOF
)

OUTPUT="$(claude -p --max-turns 3 "$PROMPT" 2>/dev/null || true)"

if [[ -z "$OUTPUT" ]]; then
  echo "check-ai-review: SKIP — no output from claude (advisory step, continuing)"
  exit 0
fi

echo "$OUTPUT"

if echo "$OUTPUT" | grep -q "CRITICAL:"; then
  echo "check-ai-review: findings above — review before opening the PR (does not block push)"
else
  echo "check-ai-review: OK — no critical findings"
fi

exit 0

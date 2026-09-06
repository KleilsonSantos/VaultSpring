#!/usr/bin/env bash
# Write a Prometheus bearer token file for scraping /actuator/prometheus.
#
# Usage:
#   bash scripts/observability-prometheus-token.sh [base-url]
#
# Requires curl and a running VaultSpring instance with seeded users.

set -euo pipefail

BASE_URL="${1:-http://localhost:8080}"
TOKEN_FILE="docker/observability/prometheus/vaultspring.token"

LOGIN_RESPONSE="$(curl -sf -X POST "${BASE_URL}/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"secret123"}')"

TOKEN="$(printf '%s' "${LOGIN_RESPONSE}" | python3 -c 'import json,sys; print(json.load(sys.stdin)["accessToken"])')"

printf '%s' "${TOKEN}" > "${TOKEN_FILE}"
chmod 600 "${TOKEN_FILE}"
echo "Wrote Prometheus bearer token to ${TOKEN_FILE}"

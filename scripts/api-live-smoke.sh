#!/usr/bin/env bash
# Live API smoke against http://localhost:8080 (app must be running).
set -euo pipefail

BASE="${BASE_URL:-http://localhost:8080}"
EMAIL="${SMOKE_EMAIL:-john@example.com}"
PASS="${SMOKE_PASSWORD:-secret123}"

section() {
  echo ""
  echo "================================================================"
  echo "$1"
  echo "================================================================"
}

section "GET /actuator/health"
curl -s -w "\nHTTP %{http_code}\n" "$BASE/actuator/health"

section "GET /actuator/prometheus (sem token — esperado 401 JSON)"
curl -s -w "\nHTTP %{http_code}\n" "$BASE/actuator/prometheus"

section "POST /api/v1/auth/login (credenciais validas)"
LOGIN_JSON=$(curl -s -w "\nHTTP:%{http_code}" -X POST "$BASE/api/v1/auth/login" \
  -H 'Content-Type: application/json' \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASS\"}")
HTTP=$(echo "$LOGIN_JSON" | grep '^HTTP:' | cut -d: -f2)
BODY=$(echo "$LOGIN_JSON" | grep -v '^HTTP:')
echo "$BODY" | python3 -m json.tool
echo "HTTP $HTTP"
TOKEN=$(echo "$BODY" | python3 -c "import sys,json; print(json.load(sys.stdin)['accessToken'])")

section "GET /api/v1/users (sem token — esperado 401 JSON)"
curl -s -w "\nHTTP %{http_code}\n" "$BASE/api/v1/users"

section "GET /api/v1/users/me (com Bearer)"
curl -s -w "\nHTTP %{http_code}\n" "$BASE/api/v1/users/me" -H "Authorization: Bearer $TOKEN"

section "GET /api/v1/users (com Bearer admin — esperado 200)"
curl -s -w "\nHTTP %{http_code}\n" "$BASE/api/v1/users" -H "Authorization: Bearer $TOKEN"

section "GET /v3/api-docs (login publico)"
curl -s "$BASE/v3/api-docs" | python3 -c "import sys,json; d=json.load(sys.stdin); print('login security:', d['paths']['/api/v1/auth/login']['post'].get('security'))"

echo ""
echo "Smoke concluido."

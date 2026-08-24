#!/usr/bin/env bash
# Seed KV v2 secrets for local Vault (Compose). Requires vault CLI, unsealed Vault, and VAULT_TOKEN.
#
# Usage:
#   export VAULT_ADDR=http://127.0.0.1:8200 VAULT_TOKEN=<token>
#   bash scripts/vault-seed-dev.sh
#
# Optional overrides:
#   DATASOURCE_URL, DATASOURCE_USERNAME, DATASOURCE_PASSWORD

set -euo pipefail

: "${VAULT_ADDR:?Set VAULT_ADDR (e.g. http://127.0.0.1:8200)}"
: "${VAULT_TOKEN:?Set VAULT_TOKEN — never commit this value}"

DATASOURCE_URL="${DATASOURCE_URL:-jdbc:postgresql://postgres:5432/users_db}"
DATASOURCE_USERNAME="${DATASOURCE_USERNAME:-admin}"
DATASOURCE_PASSWORD="${DATASOURCE_PASSWORD:-adminpass}"

if ! command -v vault >/dev/null 2>&1; then
  echo "vault CLI not found. Install HashiCorp Vault CLI or use docker exec vault-spring-vault vault ..."
  exit 1
fi

vault secrets enable -path=secret kv-v2 2>/dev/null || true

vault kv put secret/vaultspring \
  spring.datasource.url="${DATASOURCE_URL}" \
  spring.datasource.username="${DATASOURCE_USERNAME}" \
  spring.datasource.password="${DATASOURCE_PASSWORD}"

echo "KV secrets written to secret/vaultspring (path used by profile vault)"

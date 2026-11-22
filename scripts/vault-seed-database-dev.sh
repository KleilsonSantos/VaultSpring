#!/usr/bin/env bash
# Configure Vault Database Secrets Engine for local PostgreSQL (Compose).
# Requires vault CLI (or vault container), unsealed Vault, VAULT_TOKEN, and running Postgres.
#
# Usage:
#   export VAULT_ADDR=http://127.0.0.1:8200 VAULT_TOKEN=<token>
#   bash scripts/vault-seed-database-dev.sh
#
# Optional overrides:
#   POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD, VAULT_DB_ROLE (default: vaultspring-app)

set -euo pipefail

: "${VAULT_ADDR:?Set VAULT_ADDR (e.g. http://127.0.0.1:8200)}"
: "${VAULT_TOKEN:?Set VAULT_TOKEN — never commit this value}"

POSTGRES_DB="${POSTGRES_DB:-users_db}"
POSTGRES_USER="${POSTGRES_USER:-admin}"
POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-adminpass}"
VAULT_DB_ROLE="${VAULT_DB_ROLE:-vaultspring-app}"
VAULT_DB_CONFIG="${VAULT_DB_CONFIG:-vaultspring-postgresql}"
CONTAINER="${VAULT_CONTAINER:-vault-spring-vault}"

run_vault() {
  if command -v vault >/dev/null 2>&1; then
    vault "$@"
  else
    docker exec -e VAULT_ADDR="$VAULT_ADDR" -e VAULT_TOKEN="$VAULT_TOKEN" "$CONTAINER" vault "$@"
  fi
}

run_vault secrets enable database 2>/dev/null || true

run_vault write "database/config/${VAULT_DB_CONFIG}" \
  plugin_name=postgresql-database-plugin \
  allowed_roles="${VAULT_DB_ROLE}" \
  connection_url="postgresql://{{username}}:{{password}}@postgres:5432/${POSTGRES_DB}?sslmode=disable" \
  username="${POSTGRES_USER}" \
  password="${POSTGRES_PASSWORD}"

# Least-privilege grants (VS-008). Flyway migrates as POSTGRES_USER (see
# VaultFlywayMigrationCredentials), not as this role. PostgreSQL 15 does not
# grant schema CREATE to PUBLIC, and the role must not receive it.
#
# GRANT ON ALL TABLES only covers tables that already exist when Vault creates
# the role. The application DataSource is built before Flyway runs, so on an
# empty database that grant matches nothing. ALTER DEFAULT PRIVILEGES, executed
# as the same admin Flyway uses, covers tables and identity sequences that admin
# creates afterwards. Revocation drops those default privileges first; otherwise
# DROP ROLE fails on the next rotation and the leaked role keeps its grants.
run_vault write "database/roles/${VAULT_DB_ROLE}" \
  db_name="${VAULT_DB_CONFIG}" \
  creation_statements="CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' VALID UNTIL '{{expiration}}'; GRANT USAGE ON SCHEMA public TO \"{{name}}\"; GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO \"{{name}}\"; GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO \"{{name}}\"; ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO \"{{name}}\"; ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO \"{{name}}\";" \
  revocation_statements="ALTER DEFAULT PRIVILEGES IN SCHEMA public REVOKE SELECT, INSERT, UPDATE, DELETE ON TABLES FROM \"{{name}}\"; ALTER DEFAULT PRIVILEGES IN SCHEMA public REVOKE USAGE, SELECT ON SEQUENCES FROM \"{{name}}\"; REVOKE ALL PRIVILEGES ON ALL TABLES IN SCHEMA public FROM \"{{name}}\"; REVOKE ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public FROM \"{{name}}\"; REVOKE USAGE ON SCHEMA public FROM \"{{name}}\"; DROP ROLE IF EXISTS \"{{name}}\";" \
  default_ttl="1h" \
  max_ttl="24h"

echo "Database secrets engine configured (role: ${VAULT_DB_ROLE}, config: ${VAULT_DB_CONFIG})"
echo "App profile vault/prod-vault obtains spring.datasource.username/password dynamically."

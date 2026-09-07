#!/usr/bin/env bash
# Seed local Vault for JDBC (Database Secrets Engine — ADR-0005).
# Delegates to vault-seed-database-dev.sh. KV v2 static JDBC seed removed in Phase 1.
#
# Usage:
#   export VAULT_ADDR=http://127.0.0.1:8200 VAULT_TOKEN=<token>
#   bash scripts/vault-seed-dev.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec bash "${SCRIPT_DIR}/vault-seed-database-dev.sh"

#!/bin/bash

PROJECT_ROOT=$(git rev-parse --show-toplevel)
POM_FILE="$PROJECT_ROOT/pom.xml"

# Extrair a versão atual do pom.xml
CURRENT_VERSION=$(awk '
  /<parent>/ { in_parent=1 }
  /<\/parent>/ { in_parent=0 }
  /<version>/ && !in_parent {
    gsub(/.*<version>/, "", $0);
    gsub(/<\/version>.*/, "", $0);
    print $0;
    exit;
  }
' "$POM_FILE")

# Extrair a versão do último commit (snapshot ou não)
LAST_COMMIT_VERSION=$(git show HEAD:pom.xml 2>/dev/null | awk '
  /<parent>/ { in_parent=1 }
  /<\/parent>/ { in_parent=0 }
  /<version>/ && !in_parent {
    gsub(/.*<version>/, "", $0);
    gsub(/<\/version>.*/, "", $0);
    print $0;
    exit;
  }
')

# Remover -SNAPSHOT para comparação semântico-funcional
CURRENT_VERSION_CLEAN=$(echo "$CURRENT_VERSION" | sed -E 's/-SNAPSHOT//')
LAST_COMMIT_VERSION_CLEAN=$(echo "$LAST_COMMIT_VERSION" | sed -E 's/-SNAPSHOT//')

echo "📦 Versão no pom.xml: $CURRENT_VERSION"
echo "📦 Versão no último commit: $LAST_COMMIT_VERSION"

if [ "$CURRENT_VERSION_CLEAN" = "$LAST_COMMIT_VERSION_CLEAN" ] && [ "$CURRENT_VERSION" = "$LAST_COMMIT_VERSION" ]; then
  echo "❌ A versão no pom.xml não foi alterada em relação ao último commit!"
  echo "💡 Atualize a versão antes de fazer commit."
  exit 1
else
  echo "✅ Versão alterada detectada. Tudo certo para o commit!"
fi

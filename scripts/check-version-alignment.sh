#!/bin/bash

PROJECT_ROOT=$(git rev-parse --show-toplevel)
POM_FILE="$PROJECT_ROOT/pom.xml"

# Extrair a versão atual do pom.xml (sem SNAPSHOT, sem v)
POM_VERSION=$(awk '
  /<parent>/ { in_parent=1 }
  /<\/parent>/ { in_parent=0 }
  /<version>/ && !in_parent {
    gsub(/.*<version>/, "", $0);
    gsub(/<\/version>.*/, "", $0);
    print $0;
    exit;
  }
' "$POM_FILE" | sed -E 's/^v?//; s/-.*//')

# Extrair a versão do pom.xml do último commit
GIT_COMMIT_VERSION=$(git show HEAD:pom.xml 2>/dev/null | awk '
  /<parent>/ { in_parent=1 }
  /<\/parent>/ { in_parent=0 }
  /<version>/ && !in_parent {
    gsub(/.*<version>/, "", $0);
    gsub(/<\/version>.*/, "", $0);
    print $0;
    exit;
  }
' | sed -E 's/^v?//; s/-.*//')

echo "📦 Versão no pom.xml: $POM_VERSION"
echo "📦 Versão no último commit: $GIT_COMMIT_VERSION"

if [ "$POM_VERSION" = "$GIT_COMMIT_VERSION" ]; then
  echo "❌ A versão no pom.xml não foi alterada em relação ao último commit!"
  echo "💡 Atualize a versão antes de fazer commit."
  exit 1
else
  echo "✅ Versão alterada detectada. Tudo certo para o commit!"
fi

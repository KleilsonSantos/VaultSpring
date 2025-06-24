#!/bin/bash

# Get the root directory of the Git project 📂
PROJECT_ROOT=$(git rev-parse --show-toplevel)
POM_FILE="$PROJECT_ROOT/pom.xml"

# Skip the pre-commit hook if the flag is set (useful for emergencies or CI) 🚫
if [ "$SKIP_PRECOMMIT" = "true" ]; then
  echo "Pre-commit hook skipped."
  exit 0
fi

# Extract the current version from pom.xml 📄
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

# Extract the version from the last commit (including if it was a SNAPSHOT) ⏪
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

# Remove the -SNAPSHOT suffix for semantic comparison 🔍
CURRENT_VERSION_CLEAN=$(echo "$CURRENT_VERSION" | sed -E 's/-SNAPSHOT//')
LAST_COMMIT_VERSION_CLEAN=$(echo "$LAST_COMMIT_VERSION" | sed -E 's/-SNAPSHOT//')

# Show versions to help with debugging 🛠️
echo "📦 Version in pom.xml: $CURRENT_VERSION"
echo "📦 Version in last commit: $LAST_COMMIT_VERSION"

# Block the commit if the version wasn't changed ❌
if [ "$CURRENT_VERSION_CLEAN" = "$LAST_COMMIT_VERSION_CLEAN" ] && [ "$CURRENT_VERSION" = "$LAST_COMMIT_VERSION" ]; then
  echo "❌ The version in pom.xml has not been updated since the last commit!"
  echo "💡 Please update the version before committing."
  exit 1
else
  echo "✅ Version change detected. You're good to go!"
fi

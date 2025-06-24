#!/bin/bash

# 📁 Copy the version check script to the Git pre-commit hook location
cp ./scripts/check-version-alignment.sh .git/hooks/pre-commit

# 🔐 Make the script executable
chmod +x .git/hooks/pre-commit

# ✅ Confirmation message
echo "✅ Git hook installed successfully!"

#!/bin/bash
cp ./scripts/check-version-alignment.sh .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
echo "✅ Git hook instalado com sucesso!"

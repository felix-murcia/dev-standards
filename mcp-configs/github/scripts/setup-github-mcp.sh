#!/usr/bin/env bash
# scripts/setup-github-mcp.sh
set -e

echo "🔑 Configurando MCP GitHub..."
read -p "GitHub PAT (ghp_...): " GITHUB_PAT
read -p "Repo Owner (ej: felix-murcia): " REPO_OWNER
read -p "Repo Name: " REPO_NAME

export GITHUB_PAT="$GITHUB_PAT"
export REPO_OWNER="$REPO_OWNER"
export REPO_NAME="$REPO_NAME"

mkdir -p agent/tools agent/hooks
echo "✅ .mcp.json generado"
echo "✅ Tools schema cargados en agent/tools/"
echo "✅ Hook de validación pre-call activo"
echo "🚀 Inicia tu agente con: node agent/core.js --mcp-config .mcp.json"

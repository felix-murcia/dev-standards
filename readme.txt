# 1. Enlaza reglas a tu IDE (ajusta extensión según plugin)
ln -s "$(pwd)/standards/AGENTS.md" "$(pwd)/.cursorrules"

# 2. Coloca .mcp.json en la raíz de cada proyecto donde trabajes
cp mcp-configs/github/.mcp.json .mcp.json
echo 'GITHUB_PAT=ghp_...' >> .env.local

# 3. Prueba con tu asistente
# Escribe: "/check-arch" o "Refactoriza PaymentService aplicando SOLID"

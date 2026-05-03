// agent/hooks/pre-github-call.js
module.exports = async (toolCall, context) => {
  const { toolName, input } = toolCall;
  
  // 1. Validar token presente
  if (!process.env.GITHUB_PAT) {
    throw new Error("GITHUB_PAT no configurado. Ejecuta: export GITHUB_PAT='ghp_...'");
  }

  // 2. Sanitizar paths (prevención traversal)
  if (input.path && input.path.includes('..')) {
    throw new Error("Path traversal detectado en github_get_file");
  }

  // 3. Limitar queries costosas
  if (toolName === 'github_search_code' && !input.query.includes(':')) {
    context.warn("Búsqueda sin filtro de scope. Considera añadir 'language:' o 'path:' para optimizar tokens");
  }

  // 4. Registrar para audit/memory-sync
  context.logToolCall({
    timestamp: new Date().toISOString(),
    tool: toolName,
    scope: `${process.env.REPO_OWNER}/${process.env.REPO_NAME}`,
    readOnly: true
  });

  return toolCall; // Proceed
};

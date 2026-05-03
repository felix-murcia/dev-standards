# felix-murcia/dev-standards

> 📂 Centralized repository for Architecture Standards, AI Agent Rules, and Project Templates.

## 📁 Structure
- `standards/`: Universal rules (`AGENTS.md`, `ARCHITECTURE.md`) and language-specific guides.
- `templates/`: Ready-to-use boilerplates for Spring Boot, FastAPI, and Next.js.
- `skills/`: Modular knowledge packs for AI agents (progressive disclosure).
- `mcp-configs/`: Standardized MCP server configurations (GitHub, DB, etc.).

## 🚀 Quick Start
1. **New Project**: `make init-spring PROJECT_NAME=my-api`
2. **Connect Agent**: Copy `.cursorrules` to your project root.
3. **Configure MCP**: Run `bash mcp-configs/github/scripts/setup-github-mcp.sh`

## 🤖 AI Integration
- Agents must load `standards/AGENTS.md` on startup.
- Use `/check-arch` to validate compliance.
- Use `/scaffold-module [name]` to generate clean architecture modules.

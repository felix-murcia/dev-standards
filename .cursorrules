# 🤖 Felix Murcia Dev Standards

## 🏗️ Architecture
- Strict Hexagonal Architecture: `domain` (pure) → `application` (use cases) → `infrastructure` (adapters) → `presentation` (UI/API).
- Dependency Rule: Inner layers NEVER import outer layers.
- SOLID Principles: Enforce SRP, OCP, LSP, ISP, DIP in every code generation.

## 🛠️ Tech Stack Rules
- **Java/Spring**: Use Records for DTOs/ValueObjects. CompletableFuture for async flows. Constructor injection only. No Lombok if records suffice.
- **Python/FastAPI**: Use Pydantic v2 for schemas. SQLAlchemy 2.0 async. Protocol for ports. Type hints strictly enforced.
- **Next.js/React**: Server Components by default. TanStack Query for server state. Zod for validation. Tailwind for styling.

## 🧠 Agent Behavior
- Load `standards/ARCHITECTURE.md` and `standards/AGENTS.md` context before answering.
- If stack is detected (pom.xml/pyproject.toml/package.json), load specific guide from `standards/architecture/`.
- Use MCP GitHub tools to read repo context instead of asking user to paste code.
- Prioritize progressive disclosure: load skills only when relevant.

## 🚫 Prohibited
- Business logic in controllers/routers/components.
- Direct DB calls in use cases.
- `any` types in TypeScript or Python.
- Field injection in Spring.

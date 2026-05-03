# 🔗 Integración vía Git Submodule

## 1. Preparar el Repositorio Central (Una sola vez)
Asegúrate de que tu repo de estándares esté limpio y tenga una rama estable (ej: `main`).

```bash
cd ~/Public/felix-murcia/dev-standards
git add .
git commit -m "feat: initial standards release v1.0"
git push origin main
```

## 2. Inicializar un Nuevo Proyecto con el Submodule

Cuando crees un nuevo proyecto (o en uno existente), ejecuta esto en la raíz:

```bash
# Navega a tu nuevo proyecto
cd ~/projects/mi-nuevo-proyecto

# Añade el submodule en la carpeta 'standards'
git submodule add https://github.com/felix-murcia/dev-standards.git standards

# Inicializa y actualiza los archivos
git submodule update --init --recursive
```

> ✅ **Resultado:** Ahora tienes una carpeta `standards/` en tu proyecto que es un enlace directo a tu repo central. Cualquier cambio que hagas en `dev-standards` se reflejará aquí tras actualizar.

## 3. Automatizar la Configuración Local (El "Pegamento")

Como los submodules son de solo lectura (no debes editarlos dentro del proyecto hijo), necesitamos copiar/enlazar los archivos de configuración específicos a la raíz.

Crea este script en tu proyecto: `scripts/init-project.sh`

```bash
#!/usr/bin/env bash
set -e

echo "🔗 Enlazando estándares desde submodule..."

# 1. Enlazar reglas del agente (Cursor/Copilot)
ln -sf ../standards/.cursorrules .cursorrules
ln -sf ../standards/.github/copilot-instructions.md .github/copilot-instructions.md 2>/dev/null || true

# 2. Enlazar script de validación
ln -sf ../standards/scripts/validate-standards.sh scripts/validate-standards.sh
chmod +x scripts/validate-standards.sh

# 3. Copiar configuración MCP (porque requiere variables locales)
cp standards/mcp-configs/github/.mcp.json .mcp.json

echo "✅ Proyecto inicializado. Ejecuta './scripts/validate-standards.sh' para verificar."
```

Ejecútalo:
```bash
chmod +x scripts/init-project.sh
./scripts/init-project.sh
```

## 4. Flujo de Trabajo Diario

### 📥 Actualizar Estándares en tus Proyectos
Si has mejorado una regla en `dev-standards`, para aplicarla a todos tus proyectos:

```bash
cd mi-nuevo-proyecto
git submodule update --remote --merge
git add standards
git commit -m "chore: update dev-standards to latest version"
```

### 📤 Proponer Cambios a los Estándares
Si encuentras un error o mejora mientras trabajas en un proyecto:

1. Ve a la carpeta del submodule: `cd standards`
2. Haz tus cambios, commit y push allí.
3. Vuelve a la raíz del proyecto: `cd ..`
4. El submodule ahora apunta al nuevo commit. Haz commit de ese cambio en el proyecto principal.

---

## 📝 Resumen de Archivos Clave

| Archivo | Origen | Acción | Propósito |
|---------|--------|--------|-----------|
| `standards/` | Submodule | **Read-only** | Fuente de verdad de arquitectura y reglas IA. |
| `.cursorrules` | Symlink | **Auto-link** | Instrucciones inmediatas para el IDE. |
| `.mcp.json` | Copia | **Local Config** | Conexión a GitHub (con tus tokens/vars). |
| `scripts/validate-standards.sh` | Symlink | **Auto-link** | Guardián de la arquitectura en CI/CD o pre-commit. |

---

## 🚀 ¿Listo para probarlo?

1. Crea un repo vacío en GitHub llamado `test-hexagonal`.
2. Clónalo localmente.
3. Ejecuta los pasos 2 y 3 de arriba.
4. Abre Cursor/VS Code y pregunta: *"¿Cuál es la estructura de carpetas recomendada para este proyecto?"*

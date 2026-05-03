#!/usr/bin/env bash
# scripts/validate-standards.sh
# Valida la integridad arquitectónica de los proyectos en el repositorio.
# Detecta violaciones de la Regla de Dependencia (Hexagonal) y estándares de código.

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "🔍 Iniciando validación de estándares arquitectónicos..."

VIOLATIONS=0

# --- 1. Validación Java/Spring Boot ---
validate_java() {
    echo -e "\n${YELLOW}☕ Validando proyectos Java/Spring...${NC}"
    
    # Buscar archivos .java dentro de carpetas 'domain' que importen Spring o Jakarta Persistence
    if grep -r --include="*.java" -l "import org.springframework\|import jakarta.persistence" src/main/java/*/domain/ 2>/dev/null; then
        echo -e "${RED}❌ VIOLACIÓN: domain/ no puede depender de frameworks externos (Spring/JPA).${NC}"
        VIOLATIONS=$((VIOLATIONS + 1))
    else
        echo -e "${GREEN}✅ Java Domain: Limpio de dependencias externas.${NC}"
    fi

    # Verificar que los Controllers no tengan lógica de negocio compleja (heurística simple: longitud > 50 líneas o uso de @Transactional)
    if grep -r --include="*.java" -l "@Transactional" src/main/java/*/presentation/controllers/ 2>/dev/null; then
        echo -e "${RED}❌ VIOLACIÓN: @Transactional detectado en Controllers. Debe estar en Application/Infrastructure.${NC}"
        VIOLATIONS=$((VIOLATIONS + 1))
    else
        echo -e "${GREEN}✅ Java Presentation: Sin transacciones directas.${NC}"
    fi
}

# --- 2. Validación Python/FastAPI ---
validate_python() {
    echo -e "\n${YELLOW}🐍 Validando proyectos Python/FastAPI...${NC}"

    # Buscar imports de FastAPI o SQLAlchemy en domain/
    if grep -r --include="*.py" -l "from fastapi\|import fastapi\|from sqlalchemy\|import sqlalchemy" src/domain/ 2>/dev/null; then
        echo -e "${RED}❌ VIOLACIÓN: domain/ no puede importar FastAPI o SQLAlchemy.${NC}"
        VIOLATIONS=$((VIOLATIONS + 1))
    else
        echo -e "${GREEN}✅ Python Domain: Limpio de dependencias de framework.${NC}"
    fi

    # Verificar uso de typing.Protocol en ports/
    if ! grep -r --include="*.py" -q "Protocol" src/domain/ports/ 2>/dev/null; then
        echo -e "${YELLOW}⚠️ ADVERTENCIA: No se encontraron Protocolos en domain/ports/. ¿Estás usando interfaces puras?${NC}"
    else
        echo -e "${GREEN}✅ Python Ports: Uso de Protocol detectado.${NC}"
    fi
}

# --- 3. Validación Frontend/Next.js ---
validate_frontend() {
    echo -e "\n${YELLOW}⚛️ Validando proyectos Next.js/React...${NC}"

    # Buscar fetch/axios directo en components/ (debería estar en infrastructure/api/)
    if grep -r --include="*.tsx" --include="*.ts" -l "fetch(\|axios.get\|axios.post" src/presentation/components/ 2>/dev/null; then
        echo -e "${RED}❌ VIOLACIÓN: Llamadas API directas en Componentes. Usa Custom Hooks o Infrastructure adapters.${NC}"
        VIOLATIONS=$((VIOLATIONS + 1))
    else
        echo -e "${GREEN}✅ Frontend Presentation: Sin llamadas API hardcodeadas.${NC}"
    fi

    # Verificar uso de 'any' en TypeScript
    if grep -r --include="*.ts" --include="*.tsx" -n ": any" src/ 2>/dev/null | grep -v "node_modules"; then
        echo -e "${RED}❌ VIOLACIÓN: Uso de tipo 'any' detectado. Usa tipado estricto o Zod.${NC}"
        VIOLATIONS=$((VIOLATIONS + 1))
    else
        echo -e "${GREEN}✅ Frontend Types: Sin tipos 'any'.${NC}"
    fi
}

# --- Ejecución ---
if [ -d "templates/spring-api/src" ] || [ -d "src/main/java" ]; then
    validate_java
fi

if [ -d "templates/fastapi-api/src" ] || [ -d "src/domain" ]; then
    validate_python
fi

if [ -d "templates/next-frontend/src" ] || [ -d "src/app" ]; then
    validate_frontend
fi

# --- Resultado Final ---
echo -e "\n----------------------------------------"
if [ $VIOLATIONS -eq 0 ]; then
    echo -e "${GREEN}🎉 ¡Validación completada! No se encontraron violaciones críticas.${NC}"
    exit 0
else
    echo -e "${RED}🚨 Se encontraron $VIOLATIONS violaciones arquitectónicas. Revisa los logs arriba.${NC}"
    exit 1
fi

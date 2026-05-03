```markdown
# 🏗️ ARCHITECTURE.md - Estándares Universales

> 📌 Documento base para Arquitectura Hexagonal (Ports & Adapters) y principios SOLID.  
> 🔄 Versión: `1.0.0` | 🌐 Aplica a todos los lenguajes y stacks.  
> 🔗 Las guías específicas por lenguaje se cargan dinámicamente según el indicador en la raíz del proyecto.

---

## 🔀 Enrutador de Guías por Lenguaje

| Indicador en raíz del proyecto | Cargar guía específica |
|-------------------------------|------------------------|
| `pom.xml` / `build.gradle`    | `architecture/java-spring.md` |
| `pyproject.toml`              | `architecture/python-fastapi.md` |
| `package.json` + `tsconfig.json` | `architecture/typescript-node.md` |
| `composer.json`               | `architecture/php-laravel.md` |
| `go.mod`                      | `architecture/go-echo.md` |
| *Sin indicador*               | Aplicar reglas universales + solicitar confirmación al usuario |

---

## 🧭 Arquitectura Hexagonal: Reglas Universales

### 1. Capas y Responsabilidades
```yaml
domain:
  ubicación: "Núcleo central"
  contenido: "Entidades, ValueObjects, Domain Events, Reglas de Negocio puras, Interfaces (Ports)"
  prohibido: "Frameworks, DB, HTTP, UI, Serialización, Logs"

application:
  ubicación: "Casos de uso"
  contenido: "Orquestación de flujos, Validación de flujo, DTOs de entrada/salida, Coordinación de puertos"
  prohibido: "Lógica de dominio pura, Queries SQL/NoSQL, Controladores, Configuración de infraestructura"

adapters_inbound:
  ubicación: "Puertos de entrada (Presentation)"
  contenido: "APIs REST, GraphQL, CLI, WebSockets, Webhooks, Mensajería de entrada"
  regla: "Solo mapean formato externo → DTO de aplicación. Delegan ejecución a use-cases."

adapters_outbound:
  ubicación: "Puertos de salida (Infrastructure)"
  contenido: "Repositorios DB, Clientes HTTP externos, Sistemas de mensajería, Caches, File storage"
  regla: "Implementan interfaces definidas en domain/application. Nunca exponen sus modelos internos."
```

### 2. Flujo de Dependencias (Dependency Rule)
- Las dependencias **siempre apuntan hacia el centro** (`domain`).
- `adapters_inbound` → depende de → `application`
- `application` → depende de → `domain`
- `adapters_outbound` → depende de → `domain` + `application`
- **Nunca** se permite dependencia inversa. Si `domain` importa algo de `adapters/`, la arquitectura está rota.

### 3. Comunicación entre Capas
- Los `domain entities` **nunca** se serializan ni se exponen fuera de `domain`.
- Los `application DTOs` son inmutables y específicos por caso de uso.
- Los `adapters` deben traducir formatos externos ↔ DTOs usando mappers explícitos.
- Los errores se propagan hacia arriba: `domain exceptions` → `application errors` → `adapter responses`.

---

## 📐 Principios SOLID: Aplicación Universal

| Principio | Regla Obligatoria | Indicador de Violación |
|-----------|-------------------|------------------------|
| **SRP** | 1 clase/módulo = 1 razón de cambio. Máx. 3 responsabilidades públicas por módulo. | Clase con `+10` métodos públicos o que modifica DB + envía emails + loguea. |
| **OCP** | Extiende comportamiento con nuevas clases/estrategias. No modifiques código probado. | `if/switch` gigante para soportar nuevos tipos/pagos/proveedores. |
| **LSP** | Subtipos deben ser intercambiables sin alterar el comportamiento esperado del sistema. | Lanzar `NotImplementedError` o cambiar precondiciones en subclases. |
| **ISP** | Interfaces pequeñas y cohesionadas. Mejor 3 contratos de 2 métodos que 1 de 20. | Interfaces "god" con métodos que no todos los implementadores usan. |
| **DIP** | Depende de abstracciones. Configura instancias concretas en el borde externo. | `new ConcreteService()` dentro de lógica de negocio o casos de uso. |

---

## 🚦 Reglas de Inyección y Gestión de Estado

- **Inyección de Dependencias**: Siempre por constructor. Nunca field/setter injection en lógica de negocio.
- **Inmutabilidad**: DTOs, ValueObjects y Entities deben ser inmutables después de la construcción.
- **Estado Compartido**: Prohibido usar variables globales o singletons mutables para estado de negocio.
- **Transacciones**: Definidas en `application` o `adapters_outbound`. Nunca en `domain`.
- **Logging**: Solo en `adapters` o capa de aplicación transversal. `domain` no conoce logs.

---

## ✅ Checklist de Validación Arquitectónica

- [ ] ¿`domain/` está libre de imports de frameworks, DB, HTTP o UI?
- [ ] ¿Todas las dependencias externas están abstraídas en interfaces (Ports)?
- [ ] ¿Los casos de uso dependen solo de `domain` y puertos?
- [ ] ¿Los adaptadores implementan puertos sin exponer sus modelos internos?
- [ ] ¿Se aplica SRP? ¿Ninguna clase tiene >3 responsabilidades?
- [ ] ¿Se evita `if/switch` masivo? ¿Se usan polimorfismo/estrategias?
- [ ] ¿Las interfaces son específicas? ¿Ningún implementador deja métodos vacíos?
- [ ] ¿La inyección es por constructor? ¿No hay `new` directo en lógica crítica?
- [ ] ¿Los DTOs son inmutables y específicos por flujo?
- [ ] ¿El flujo de dependencias es estrictamente inward?

---

## ⚡ Comandos de Agente

- `/detect-stack` → Identifica lenguaje y carga guía específica
- `/check-arch` → Valida estructura actual contra reglas universales + guía cargada
- `/scaffold-layer [layer] [module]` → Genera esqueleto de capa/módulo respetando hexagonal + SOLID
- `/refactor-violation [file]` → Sugiere corrección automática para violación SOLID o de flujo
- `/explain-port` → Explica qué interfaz define qué contrato y quién debe implementarlo

---


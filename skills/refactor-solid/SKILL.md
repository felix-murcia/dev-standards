# 🛠️ Refactor Solid Skill
## 📌 Cuándo activarse
- Usuario menciona: "SOLID", "refactor", "acoplamiento", "clase larga", "demasiados if"
## 🧠 Instrucciones
1. Identifica violación SRP: clase > 1 responsabilidad → extrae a nueva clase/interfaz
2. Detecta OCP: `if/switch` por tipo → aplica Strategy/Polymorphism
3. Valida LSP: subclase rompe contrato de base → ajusta pre/postcondiciones
4. Revisa ISP: interfaz con métodos no usados → divide en contratos pequeños
5. Verifica DIP: `new Concrete()` en lógica → inyecta interfaz por constructor
## 🧪 Validación
- Ejecuta tests unitarios antes y después
- Asegura cobertura > 80% en dominio
- Mantén tests verdes en cada paso

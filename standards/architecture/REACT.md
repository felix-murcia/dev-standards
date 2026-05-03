```markdown
# 🎨 Frontend (React/TypeScript) - Architecture Guide

> 📌 Extends: `standards/ARCHITECTURE.md` (Hexagonal + SOLID Universal)  
> 🔄 Stack: React 18+ | TypeScript 5.x | Next.js/Vite | TanStack Query | Zod | Zustand/Context  
> 📦 Indicador: `package.json` + `tsconfig.json`

---

## 📁 Estructura Obligatoria
```
src/
├── domain/                      # 🧠 Núcleo puro (0 deps de React/DOM/APIs)
│   ├── entities/                # Interfaces/Types estrictos
│   ├── valueobjects/            # Zod schemas + parseo seguro
│   ├── rules/                   # Funciones puras de validación/negocio
│   └── ports/                   # Interfaces para infraestructura
│
├── application/                 # ⚙️ Orquestación y estado
│   ├── usecases/                # Funciones async/await atómicas
│   ├── hooks/                   # Custom hooks de lógica pura
│   └── stores/                  # Zustand/Redux adapters
│
├── presentation/                # 🟢 Inbound Adapter
│   ├── pages/ | app/            # Rutas y layouts
│   ├── components/              # UI pura (dumb + smart separados)
│   ├── layouts/
│   └── providers/               # Theme, Query, Auth, DI
│
└── infrastructure/              # 🔵 Outbound Adapter
    ├── api/                     # Axios/Fetch clients, interceptors
    ├── mappers/                 # API payload ↔ Domain entity
    ├── storage/                 # localStorage/sessionStorage/IndexedDB
    └── config/                  # Env vars, feature flags, constants
```

---

## 🔑 Implementación por Capa

### Domain (Entities & Validation)
```typescript
// domain/entities/user.ts
export interface User {
  readonly id: string;
  readonly email: string;
  readonly name: string;
  readonly createdAt: Date;
}

// domain/rules/user.rules.ts
export const isEmailVerified = (user: User): boolean => 
  user.email.endsWith('@company.com') && user.createdAt < new Date('2024-01-01');

// domain/ports/api.ports.ts
export interface UserApiPort {
  fetchUser(id: string): Promise<User>;
  updateUser(id: string, payload: Partial<Pick<User, 'name'>>): Promise<User>;
}
```

### Application (Use Cases & State)
```typescript
// application/usecases/user.usecases.ts
import { UserApiPort } from '../../domain/ports/api.ports';
import { mapApiToDomain, mapDomainToApi } from '../../infrastructure/mappers/user.mapper';

export const createUserUseCase = async (
  apiPort: UserApiPort,
  payload: { name: string; email: string }
) => {
  const apiPayload = mapDomainToApi(payload);
  const raw = await apiPort.createUser(apiPayload);
  return mapApiToDomain(raw);
};

// application/hooks/useUserManagement.ts
export const useUserManagement = (apiPort: UserApiPort) => {
  const { data, mutateAsync, isPending } = useMutation({
    mutationFn: (payload: { name: string }) => createUserUseCase(apiPort, payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['users'] })
  });
  return { createUser: mutateAsync, isLoading: isPending, data };
};
```

### Presentation (Components)
```typescript
// presentation/components/UserForm.tsx
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { userSchema } from '../../domain/valueobjects/user.schema';
import { useUserManagement } from '../../application/hooks/useUserManagement';
import { userApiPort } from '../../infrastructure/api/user.api';

export const UserForm = () => {
  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: zodResolver(userSchema)
  });
  const { createUser, isLoading } = useUserManagement(userApiPort);

  const onSubmit = handleSubmit(async (data) => {
    await createUser({ name: data.name });
  });

  return (
    <form onSubmit={onSubmit} className="space-y-4">
      <input {...register('name')} aria-invalid={!!errors.name} />
      {errors.name && <span role="alert">{errors.name.message}</span>}
      <button type="submit" disabled={isLoading}>
        {isLoading ? <Spinner /> : 'Create User'}
      </button>
    </form>
  );
};
```

---

## 🌐 Integración API & Estado (Modern Frontend)

### API Client (Infrastructure)
```typescript
// infrastructure/api/client.ts
import axios from 'axios';

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 5000,
  headers: { 'Content-Type': 'application/json' }
});

apiClient.interceptors.response.use(
  res => res.data,
  err => Promise.reject(new APIError(err.response?.status ?? 500, err.message))
);

// infrastructure/api/user.api.ts
export const userApiPort: UserApiPort = {
  fetchUser: (id: string) => apiClient.get<UserApiPayload>(`/users/${id}`),
  updateUser: (id, payload) => apiClient.patch<UserApiPayload>(`/users/${id}`, payload)
};
```

### State Orchestration Pattern
```typescript
// Uso de TanStack Query + Suspense + Error Boundaries
// presentation/providers/query-provider.tsx
export const QueryProvider = ({ children }: PropsWithChildren) => (
  <QueryClientProvider client={queryClient}>
    <ErrorBoundary fallback={<GlobalError />}>
      <Suspense fallback={<PageSkeleton />}>
        {children}
      </Suspense>
    </ErrorBoundary>
  </QueryClientProvider>
);
```

---

## 📐 SOLID + Modern Frontend Practices

| Principio | Implementación Concreta |
|-----------|------------------------|
| **SRP** | 1 componente = 1 responsabilidad visual. Hooks separan lógica de UI. Mappers separan transformación de datos. |
| **OCP** | Composición de hooks (`useAuth` → `useProtectedRoute`). `render props`/HOCs para extender sin modificar. |
| **LSP** | Componentes que aceptan `Props` deben respetar contrato base. No romper layout al extender. |
| **ISP** | Interfaces de API divididas: `UserReadPort`, `UserWritePort`. No pasar `useContext()` masivo a componentes que no lo necesitan. |
| **DIP** | Inversión vía `createContext` + providers o inyección explícita en hooks. Nunca importar `fetch`/`axios` directamente en componentes. |

### 🛠️ Facilidades Obligatorias (TS/React Moderno)
- `as const`, `satisfies`, `const type parameters` para inferencia precisa.
- `?.` (optional chaining), `??` (nullish coalescing) en lugar de `&&`/`||` para fallbacks.
- `async/await` + `Promise.allSettled()` para operaciones paralelas seguras.
- `React.memo`, `useMemo`, `useCallback` solo donde hay rendimiento medible.
- Zod + `@hookform/resolvers` para validación runtime segura.
- Utility types: `Partial`, `Required`, `Pick`, `Omit`, `Record`, `ReturnType`.
- `never` type para exhaustividad en `switch`/pattern matching de estados UI.
- `import()` dynamic para code-splitting de rutas/componentes pesados.

---

## 🧪 Estrategia de Testing

```yaml
unit_tests:
  scope: "domain/ + application/"
  setup: "Vitest + @testing-library/react-hooks"
  rule: "0 renders, 0 DOM, solo funciones puras y hooks mockeados"

component_tests:
  scope: "presentation/components/"
  setup: "Jest/Vitest + React Testing Library + MSW (Mock Service Worker)"
  rule: "Simular interacciones de usuario, verificar accesibilidad y states visuales"

integration_tests:
  scope: "pages/ + routes/"
  setup: "Playwright/Cypress + MSW + Testcontainers (si hay BFF)"
  rule: "Flujos completos: navegación, formularios, carga asíncrona, error recovery"
```

---

## 🚨 Violaciones a Rechazar (Agente)

- `fetch`/`axios` importados directamente en componentes o hooks de UI.
- Lógica de negocio (validaciones, cálculos, transformaciones) dentro de JSX o handlers inline.
- Uso de `any`, `object`, o `Function` como tipos.
- Prop drilling > 2 niveles (usar Context, Zustand o composición).
- Efectos secundarios en render (`useEffect` sin cleanup o dependencias inestables).
- `useMemo`/`useCallback` aplicados prematuramente (sin métricas de rendimiento).
- Promesas no manejadas (sin `try/catch`, `errorBoundary`, o `.catch()`).
- Estados de UI mezclados con estados de servidor (usar TanStack Query para server state).

---

## ✅ Checklist de Validación

- [ ] `domain/` no importa `react`, `next`, `axios` ni APIs del navegador.
- [ ] Validaciones de formulario usan Zod + resolución segura.
- [ ] Datos de servidor gestionados con TanStack Query/RTK Query (no `useState` + `useEffect`).
- [ ] Componentes puros reciben solo props inmutables; sin efectos colaterales en render.
- [ ] Mappers explícitos entre API payloads y domain entities.
- [ ] Tipado estricto (`strict: true` en `tsconfig.json`), sin `any` ni type assertions innecesarias.
- [ ] Manejo de estados async: loading, success, error, empty cubiertos UI.
- [ ] Code-splitting aplicado en rutas > 50KB o componentes pesados.
- [ ] Tests de dominio ejecutan sin levantar entorno React ni DOM.

---

## ⚡ Comandos de Agente

- `/scaffold-feature [name]` → Genera domain/app/presentation/infrastructure para feature completa
- `/add-api-client [endpoint]` → Crea client tipado, interceptores y mapper automático
- `/check-type-safety [file]` → Detecta `any`, type assertions inseguras, props mal tipadas
- `/refactor-to-zod [form]` → Migra validación manual a schema Zod + resolver
- `/optimize-renders [component]` → Sugiere `memo`, `useCallback`, virtualización o code-splitting
- `/async-flow [action]` → Genera patrón seguro: query/mutation + loading/error/empty states
```

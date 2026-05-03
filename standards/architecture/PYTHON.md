```markdown
# 🐍 Python FastAPI + BBDD - Architecture Guide

> 📌 Extends: `standards/ARCHITECTURE.md` (Hexagonal + SOLID Universal)  
> 🔄 Stack: Python 3.10+ | FastAPI | SQLAlchemy 2.0 | Alembic | Pydantic v2  
> 📦 Indicador: `pyproject.toml` o `requirements.txt`

---

## 📁 Estructura Obligatoria
```
src/
├── __init__.py
├── main.py                      # FastAPI app instance + lifespan
│
├── domain/                      # 🧠 Núcleo puro (0 deps externas)
│   ├── entities/
│   ├── value_objects/
│   ├── events/
│   ├── exceptions/
│   └── ports/
│       └── repositories.py      # typing.Protocol definitions
│
├── application/                 # ⚙️ Casos de uso
│   ├── use_cases/
│   ├── dtos/                    # Pydantic v2 (solo lectura)
│   └── services/
│
├── presentation/                # 🟢 Inbound Adapter
│   ├── routers/
│   ├── schemas/                 # Pydantic v2 (request/response)
│   ├── dependencies/            # FastAPI Deps (auth, logging, di)
│   └── middleware/
│
└── infrastructure/              # 🔵 Outbound Adapter
    ├── persistence/
    │   ├── models/              # SQLAlchemy 2.0 declarative
    │   ├── repositories/        # Protocol implementations
    │   └── mappers/             # SQLAlchemy ↔ Domain translation
    ├── database.py              # Session factory, engine, async/sync
    ├── external/                # HTTP clients, queues, storage
    └── config/                  # pydantic-settings, DI container
```

---

## 🔑 Implementación por Capa

### Domain (Ports & Entities)
```python
# domain/ports/repositories.py
from typing import Protocol, Optional, runtime_checkable
from domain.entities.user import User
from domain.value_objects.email import Email

@runtime_checkable
class UserRepositoryPort(Protocol):
    def save(self, user: User) -> User: ...
    def find_by_id(self, user_id: str) -> Optional[User]: ...
    def find_by_email(self, email: Email) -> Optional[User]: ...
```

### Application (Use Case)
```python
# application/use_cases/create_user.py
from dataclasses import dataclass
from domain.ports.repositories import UserRepositoryPort
from application.dtos.user import CreateUserDTO, UserResponseDTO

@dataclass(frozen=True)
class CreateUserUseCase:
    repo: UserRepositoryPort

    def execute(self, dto: CreateUserDTO) -> UserResponseDTO:
        if self.repo.find_by_email(dto.email):
            raise EmailAlreadyExistsError(dto.email)
        
        user = User.create(email=dto.email, name=dto.name)
        saved = self.repo.save(user)
        return UserResponseDTO.from_entity(saved)
```

### Presentation (FastAPI Router)
```python
# presentation/routers/users.py
from fastapi import APIRouter, Depends
from presentation.schemas.user import UserCreateSchema, UserResponseSchema
from presentation.dependencies import get_create_user_use_case

router = APIRouter(prefix="/users", tags=["users"])

@router.post("", status_code=201, response_model=UserResponseSchema)
async def create_user(
    payload: UserCreateSchema,
    use_case: CreateUserUseCase = Depends(get_create_user_use_case)
):
    dto = CreateUserDTO(email=payload.email, name=payload.name)
    return use_case.execute(dto)
```

---

## 💧 Base de Datos (Outbound Adapter)

### Session & Engine
```python
# infrastructure/database.py
from sqlalchemy.ext.asyncio import create_async_engine, async_sessionmaker, AsyncSession

engine = create_async_engine(settings.DB_URL, echo=False, pool_pre_ping=True)
session_factory = async_sessionmaker(engine, class_=AsyncSession, expire_on_commit=False)

async def get_db_session() -> AsyncSession:
    async with session_factory() as session:
        yield session
```

### Mapper (SQLAlchemy ↔ Domain)
```python
# infrastructure/persistence/mappers/user_mapper.py
from infrastructure.persistence.models.user_model import UserModel
from domain.entities.user import User
from domain.value_objects.email import Email

class UserMapper:
    @staticmethod
    def to_domain(model: UserModel) -> User:
        return User(
            id=model.id,
            email=Email(model.email),
            name=model.name,
            created_at=model.created_at
        )

    @staticmethod
    def to_model(entity: User) -> UserModel:
        return UserModel(
            id=entity.id,
            email=entity.email.value,
            name=entity.name,
            created_at=entity.created_at
        )
```

### Repository Implementation
```python
# infrastructure/persistence/repositories/user_repository.py
from sqlalchemy.ext.asyncio import AsyncSession
from domain.ports.repositories import UserRepositoryPort
from domain.entities.user import User
from domain.value_objects.email import Email
from infrastructure.persistence.mappers.user_mapper import UserMapper
from infrastructure.persistence.models.user_model import UserModel

class SQLAlchemyUserRepository(UserRepositoryPort):
    def __init__(self, session: AsyncSession):
        self._session = session
        self._mapper = UserMapper()

    async def save(self, user: User) -> User:
        model = self._mapper.to_model(user)
        self._session.add(model)
        await self._session.commit()
        await self._session.refresh(model)
        return self._mapper.to_domain(model)

    async def find_by_email(self, email: Email) -> User | None:
        result = await self._session.execute(
            UserModel.select().where(UserModel.email == email.value)
        )
        model = result.scalar_one_or_none()
        return self._mapper.to_domain(model) if model else None
```

---

## 📐 SOLID en Python/FastAPI

| Principio | Implementación Concreta |
|-----------|------------------------|
| **SRP** | 1 archivo = 1 entidad/use case. Routers solo mapean HTTP. Repos solo persisten. |
| **OCP** | Usa `typing.Protocol` + estrategias. Ej: `PaymentStrategy` con `Stripe`, `PayPal`. |
| **LSP** | Subtipos de `Protocol` no lanzan `NotImplementedError` ni cambian firmas. |
| **ISP** | Separa puertos: `UserReaderPort`, `UserWriterPort`, `UserNotifierPort`. |
| **DIP** | `infrastructure/dependencies.py` resuelve protocolos con implementaciones concretas. |

---

## 🧪 Estrategia de Testing

```yaml
unit_tests:
  scope: "domain/ + application/"
  setup: "pytest + dataclasses falsas + Protocol mocks (unittest.mock)"
  rule: "0 imports de FastAPI, SQLAlchemy o Pydantic"

integration_tests:
  scope: "presentation/ + infrastructure/"
  setup: "pytest-asyncio + httpx.AsyncClient + Testcontainers (PostgreSQL)"
  rule: "Usa app.dependency_overrides para inyectar session de test"

migration_tests:
  scope: "alembic/"
  setup: "alembic upgrade head + downgrade base + assert schema diff"
  rule: "Cada migración debe ser reversible y idempotente"
```

---

## 🚨 Violaciones a Rechazar (Agente)

- `import sqlalchemy` o `import pydantic` en `domain/`
- Entidades de dominio decoradas con `@dataclass` de SQLAlchemy o `BaseModel`
- Lógica de negocio dentro de FastAPI routers o endpoints
- Uso de `Session.query()` directo en use cases (rompe DIP)
- `try/except` genérico sin log estructurado o re-lanzamiento
- Retornar objetos `SQLModel`/`BaseModel` directamente desde controllers sin mapeo

---

## ✅ Checklist de Validación

- [ ] `domain/ports/` solo usa `typing.Protocol`
- [ ] `infrastructure/persistence/` implementa protocolos, no los define
- [ ] Mappers explícitos entre `SQLAlchemy models` ↔ `domain entities`
- [ ] FastAPI `Depends()` inyecta use cases, no repositorios directamente
- [ ] Pydantic v2 solo en `presentation/schemas/` y `application/dtos/`
- [ ] Transacciones gestionadas en `infrastructure/database.py` o middleware
- [ ] Tests de dominio sin `pytest-asyncio` ni `Testcontainers`
- [ ] Alembic configurado con `script_location` relativo a `infrastructure/`

---

## ⚡ Comandos de Agente

- `/scaffold-fastapi [name]` → Genera módulo FastAPI completo (router, schema, use case, port, repo)
- `/add-db-model [entity]` → Crea SQLAlchemy model + mapper + repo skeleton
- `/check-di-wiring` → Valida que `infrastructure/dependencies.py` resuelve todos los protocolos
- `/generate-alembic-revision [desc]` → Crea migración segura con downgrade implícito
- `/refactor-to-protocol [class]` → Extrae interfaz Protocol de implementación concreta
```

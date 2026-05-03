# templates/fastapi-api/src/infrastructure/persistence/repositories/user_repository.py
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from src.domain.entities.user import User
from src.domain.ports.repositories import UserRepositoryPort
from src.domain.value_objects.email import Email
from src.infrastructure.persistence.models.user_model import UserModel
from src.infrastructure.persistence.mappers.user_mapper import UserMapper
from uuid import UUID

class SQLAlchemyUserRepository(UserRepositoryPort):
    def __init__(self, session: AsyncSession):
        self.session = session
        self.mapper = UserMapper()

    async def save(self, user: User) -> User:
        model = self.mapper.to_model(user)
        self.session.add(model)
        await self.session.commit()
        await self.session.refresh(model)
        return self.mapper.to_domain(model)

    async def find_by_email(self, email: Email) -> User | None:
        result = await self.session.execute(select(UserModel).where(UserModel.email == email.value))
        model = result.scalar_one_or_none()
        return self.mapper.to_domain(model) if model else None

    async def exists_by_email(self, email: Email) -> bool:
        result = await self.session.execute(select(UserModel).where(UserModel.email == email.value))
        return result.scalar_one_or_none() is not None

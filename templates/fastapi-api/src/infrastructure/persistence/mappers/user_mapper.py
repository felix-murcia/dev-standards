# templates/fastapi-api/src/infrastructure/persistence/mappers/user_mapper.py
from src.domain.entities.user import User
from src.domain.value_objects.email import Email
from src.infrastructure.persistence.models.user_model import UserModel

class UserMapper:
    @staticmethod
    def to_domain(model: UserModel) -> User:
        return User(
            id=model.id,
            email=Email(model.email),
            password_hash=model.password_hash,
            name=model.name,
            created_at=model.created_at
        )

    @staticmethod
    def to_model(entity: User) -> UserModel:
        return UserModel(
            id=entity.id,
            email=entity.email.value,
            password_hash=entity.password_hash,
            name=entity.name,
            created_at=entity.created_at
        )

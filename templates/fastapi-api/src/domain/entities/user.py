# templates/fastapi-api/src/domain/entities/user.py
from dataclasses import dataclass, field
from datetime import datetime
from uuid import UUID, uuid4
from src.domain.value_objects.email import Email

@dataclass(frozen=True)
class User:
    id: UUID = field(default_factory=uuid4)
    email: Email = None
    password_hash: str = ""
    name: str = ""
    created_at: datetime = field(default_factory=datetime.utcnow)

    @classmethod
    def create(cls, email: Email, password_hash: str, name: str) -> "User":
        return cls(email=email, password_hash=password_hash, name=name)

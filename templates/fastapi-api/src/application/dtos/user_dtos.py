# templates/fastapi-api/src/application/dtos/user_dtos.py
from pydantic import BaseModel, EmailStr, Field
from uuid import UUID
from datetime import datetime

class CreateUserRequest(BaseModel):
    email: EmailStr
    password: str = Field(..., min_length=8)
    name: str

class UserResponse(BaseModel):
    id: UUID
    email: str
    name: str
    created_at: datetime

    @classmethod
    def from_entity(cls, user) -> "UserResponse":
        return cls(id=user.id, email=user.email.value, name=user.name, created_at=user.created_at)

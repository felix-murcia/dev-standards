# templates/fastapi-api/src/presentation/schemas/user_schemas.py
from pydantic import BaseModel, EmailStr, Field

class UserCreateSchema(BaseModel):
    email: EmailStr
    password: str = Field(..., min_length=8)
    name: str

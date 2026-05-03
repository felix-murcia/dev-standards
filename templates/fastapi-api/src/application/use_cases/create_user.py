# templates/fastapi-api/src/application/use_cases/create_user.py
from dataclasses import dataclass
from src.application.dtos.user_dtos import CreateUserRequest, UserResponse
from src.domain.entities.user import User
from src.domain.exceptions.user_exceptions import UserAlreadyExistsError
from src.domain.ports.repositories import UserRepositoryPort
from passlib.context import CryptContext

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

@dataclass
class CreateUserUseCase:
    repo: UserRepositoryPort

    async def execute(self, request: CreateUserRequest) -> UserResponse:
        email_vo = UserEmail(request.email) # Assuming import or wrapper
        
        if await self.repo.exists_by_email(email_vo):
            raise UserAlreadyExistsError(request.email)
        
        hashed = pwd_context.hash(request.password)
        user = User.create(email=email_vo, password_hash=hashed, name=request.name)
        saved = await self.repo.save(user)
        return UserResponse.from_entity(saved)

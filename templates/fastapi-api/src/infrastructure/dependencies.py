# templates/fastapi-api/src/infrastructure/dependencies.py
from fastapi import Depends
from src.infrastructure.database import get_db_session
from src.infrastructure.persistence.repositories.user_repository import SQLAlchemyUserRepository
from src.application.use_cases.create_user import CreateUserUseCase

def get_user_repo(session = Depends(get_db_session)):
    return SQLAlchemyUserRepository(session)

def get_create_user_use_case(repo = Depends(get_user_repo)):
    return CreateUserUseCase(repo=repo)

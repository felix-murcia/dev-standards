# templates/fastapi-api/tests/unit/test_create_user.py
import pytest
from unittest.mock import AsyncMock
from src.application.use_cases.create_user import CreateUserUseCase
from src.application.dtos.user_dtos import CreateUserRequest
from src.domain.exceptions.user_exceptions import UserAlreadyExistsError

@pytest.mark.asyncio
async def test_should_create_user():
    repo = AsyncMock()
    repo.exists_by_email.return_value = False
    repo.save.return_value = None # Simplified for brevity
    
    use_case = CreateUserUseCase(repo=repo)
    req = CreateUserRequest(email="test@test.com", password="pass1234", name="Test")
    
    # In real test, you'd mock the return of save to be a valid User entity
    # This is a skeleton to show structure

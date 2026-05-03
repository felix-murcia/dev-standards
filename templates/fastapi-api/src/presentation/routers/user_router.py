# templates/fastapi-api/src/presentation/routers/user_router.py
from fastapi import APIRouter, Depends, HTTPException, status
from src.application.dtos.user_dtos import CreateUserRequest, UserResponse
from src.application.use_cases.create_user import CreateUserUseCase
from src.presentation.schemas.user_schemas import UserCreateSchema
from src.infrastructure.dependencies import get_create_user_use_case

router = APIRouter(prefix="/users", tags=["users"])

@router.post("", status_code=status.HTTP_201_CREATED, response_model=UserResponse)
async def create_user(
    payload: UserCreateSchema,
    use_case: CreateUserUseCase = Depends(get_create_user_use_case)
):
    try:
        dto = CreateUserRequest(**payload.model_dump())
        return await use_case.execute(dto)
    except Exception as e:
        raise HTTPException(status_code=409 if "already exists" in str(e) else 500, detail=str(e))

# templates/fastapi-api/src/main.py
from fastapi import FastAPI
from src.presentation.routers import user_router
from src.infrastructure.config.settings import settings

app = FastAPI(title=settings.APP_NAME, version="1.0.0")
app.include_router(user_router.router, prefix="/api/v1")

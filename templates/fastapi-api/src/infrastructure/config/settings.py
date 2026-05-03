# templates/fastapi-api/src/infrastructure/config/settings.py
from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    APP_NAME: str = "FastAPI Clean Arch"
    DB_URL: str = "postgresql+asyncpg://dev:secret@localhost:5432/app_db"
    
    class Config:
        env_file = ".env"

settings = Settings()

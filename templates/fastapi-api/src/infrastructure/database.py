# templates/fastapi-api/src/infrastructure/database.py
from sqlalchemy.ext.asyncio import create_async_engine, async_sessionmaker, AsyncSession
from src.infrastructure.config.settings import settings

engine = create_async_engine(settings.DB_URL, echo=False)
session_factory = async_sessionmaker(engine, class_=AsyncSession, expire_on_commit=False)

async def get_db_session() -> AsyncSession:
    async with session_factory() as session:
        yield session

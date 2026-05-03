# templates/fastapi-api/src/domain/exceptions/user_exceptions.py
class DomainException(Exception):
    pass

class UserAlreadyExistsError(DomainException):
    def __init__(self, email: str):
        super().__init__(f"User with email {email} already exists")

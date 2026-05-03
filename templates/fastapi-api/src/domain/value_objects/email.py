# templates/fastapi-api/src/domain/value_objects/email.py
import re

class Email:
    def __init__(self, value: str):
        pattern = r"^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$"
        if not re.match(pattern, value):
            raise ValueError(f"Invalid email format: {value}")
        self.value = value.lower().strip()

    def __eq__(self, other):
        return isinstance(other, Email) and self.value == other.value
    
    def __hash__(self):
        return hash(self.value)

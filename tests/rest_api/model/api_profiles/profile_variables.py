from pydantic import BaseModel


class ProfileVariables(BaseModel):
    name: str
    object: str
    defaultValue: str
    value: str

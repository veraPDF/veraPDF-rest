from pydantic import BaseModel


class ApiEndpoint(BaseModel):
    id: str
    version: str
    buildDate: int

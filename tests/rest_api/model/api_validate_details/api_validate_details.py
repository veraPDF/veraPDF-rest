from pydantic import BaseModel


class ApiValidateDetails(BaseModel):
    id: str
    name: str
    version: str
    provider: str
    description: str

from pydantic import BaseModel


class OS(BaseModel):
    name: str
    version: str
    architecture: str

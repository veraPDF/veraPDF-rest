from pydantic import BaseModel


class Java(BaseModel):
    vendor: str
    version: str
    architecture: str
    home: str

from pydantic import BaseModel


class Profile(BaseModel):
    name: str
    description: str
    creator: str
    dateCreated: int

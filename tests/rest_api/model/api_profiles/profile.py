from pydantic import BaseModel
from pydantic_xml import BaseXmlModel


class Profile(BaseModel):
    name: str
    description: str
    creator: str
    dateCreated: int


class ProfileXMl(BaseXmlModel):
    name: str
    description: str
    creator: str
    dateCreated: int

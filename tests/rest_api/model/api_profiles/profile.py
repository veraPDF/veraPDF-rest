from typing import List

from pydantic import BaseModel
from pydantic_xml import BaseXmlModel, element


class Profile(BaseModel):
    name: str
    description: str
    creator: str
    dateCreated: int


class ProfileXMl(BaseXmlModel):
    name: str = element(tag="name")
    description: str = element(tag="description")
    creator: str = element(tag="creator")
    dateCreated: int = element(tag="dateCreated")


class ProfilesXMl(
    BaseXmlModel,
    tag="HashSet",
    search_mode="unordered",
):
    items: List[ProfileXMl] = element(tag="item")

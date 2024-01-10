from pydantic import BaseModel
from pydantic_xml import BaseXmlModel, element


class OS(BaseModel):
    name: str
    version: str
    architecture: str


class OSXml(BaseXmlModel, tag="os"):
    name: str = element(tag="name")
    version: str = element(tag="version")
    architecture: str = element(tag="architecture")

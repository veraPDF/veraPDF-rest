from pydantic import BaseModel
from pydantic_xml import BaseXmlModel, element


class Java(BaseModel):
    vendor: str
    version: str
    architecture: str
    home: str


class JavaXml(BaseXmlModel, tag="java"):
    vendor: str = element(tag="vendor")
    version: str = element(tag="version")
    architecture: str = element(tag="architecture")
    home: str = element(tag="home")

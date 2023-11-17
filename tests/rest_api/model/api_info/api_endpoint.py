from pydantic import BaseModel
from pydantic_xml import element
from pydantic_xml import BaseXmlModel


class ApiEndpoint(BaseModel):
    id: str
    version: str
    buildDate: int


class ApiEndpointXml(BaseXmlModel, tag="ReleaseDetails"):
    id: str = element(tag="id")
    version: str = element(tag="version")
    buildDate: int = element(tag="buildDate")

from pydantic import BaseModel
from pydantic_xml import BaseXmlModel, element


class Server(BaseModel):
    ipAddress: str
    hostName: str
    machAddress: str


class ServerXml(BaseXmlModel, tag="server"):
    ipAddress: str = element(tag="ipAddress")
    hostName: str = element(tag="hostName")
    machAddress: str = element(tag="machAddress")

from pydantic_xml import BaseXmlModel
from pydantic import BaseModel
from tests.rest_api.model.api_info.java import JavaXml
from tests.rest_api.model.api_info.os import OSXml
from tests.rest_api.model.api_info.server import ServerXml


from tests.rest_api.model.api_info.java import Java
from tests.rest_api.model.api_info.os import OS
from tests.rest_api.model.api_info.server import Server


class Environment(BaseModel):
    java: Java
    os: OS
    server: Server


class EnvironmentXml(
    BaseXmlModel,
    tag="Environment",
    # search_mode="unordered",
):
    os: OSXml
    java: JavaXml
    server: ServerXml

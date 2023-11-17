from pydantic_xml import BaseXmlModel
from tests.rest_api.model.api_info.java import JavaXml
from tests.rest_api.model.api_info.os import OSXml
from tests.rest_api.model.api_info.server import ServerXml


class Environment(
    BaseXmlModel,
    tag="Environment",
    # search_mode="unordered",
):
    os: OSXml
    java: JavaXml
    server: ServerXml

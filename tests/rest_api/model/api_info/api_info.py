from pydantic import BaseModel

from tests.rest_api.model.api_info.java import Java
from tests.rest_api.model.api_info.os import OS
from tests.rest_api.model.api_info.server import Server


class ApiInfo(BaseModel):
    java: Java
    os: OS
    server: Server

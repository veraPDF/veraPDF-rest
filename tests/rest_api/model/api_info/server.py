from pydantic import BaseModel


class Server(BaseModel):
    ipAddress: str
    hostName: str
    machAddress: str

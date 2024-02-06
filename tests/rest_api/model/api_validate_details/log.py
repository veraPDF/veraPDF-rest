from typing import List

from pydantic import BaseModel


class Log(BaseModel):
    occurrences: int
    level: str
    message: str


class Logs(BaseModel):
    logsCount: int
    logs: List[Log]

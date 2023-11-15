from pydantic import BaseModel


class RuleID(BaseModel):
    specification: str
    clause: str
    testNumber: int

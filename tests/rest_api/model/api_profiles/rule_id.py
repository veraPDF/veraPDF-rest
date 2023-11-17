from pydantic import BaseModel
from pydantic_xml import BaseXmlModel, element


class RuleID(BaseModel):
    specification: str
    clause: str
    testNumber: int


class RuleIDXml(
    BaseXmlModel,
    search_mode="unordered",
):
    specification: str = element(tag="specification")
    clause: str = element(tag="clause")
    testNumber: int = element(tag="testNumber")

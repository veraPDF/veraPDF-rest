from typing import List

from pydantic import BaseModel
from pydantic_xml import BaseXmlModel, element


class RuleID(BaseModel):
    clause: str
    specification: str
    testNumber: int


class RuleIDXml(
    BaseXmlModel,
):
    specification: str = element(tag="specification")
    clause: str = element(tag="clause")
    testNumber: int = element(tag="testNumber")


class RulesXml(
    BaseXmlModel,
    tag="TreeSet",
    search_mode="unordered",
):
    items: List[RuleIDXml] = element(tag="item")

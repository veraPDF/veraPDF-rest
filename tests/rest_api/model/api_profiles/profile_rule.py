from typing import List

from pydantic import BaseModel
from pydantic_xml import BaseXmlModel, element

from tests.rest_api.model.api_profiles.rule_id import RuleID, RuleIDXml


class ProfileRule(BaseModel):
    object: str
    # deferred: str
    # tags: str
    description: str
    test: str
    # error:
    # references: List[str] = []
    ruleId: RuleID
    # tagsSet: List[str] = []


class ProfileRuleXml(
    BaseXmlModel,
    search_mode="unordered",
):
    object: str = element(tag="object")
    # deferred: str = element(tag="deferred")
    # tags: str = element(tag="id")
    description: str = element(tag="description")
    # test: str = element(tag="test")
    # error:
    # references: List[str] = []
    ruleId: RuleIDXml
    # tagsSet: List[str] = []


class ProfileRuleSXml(
    BaseXmlModel,
    tag="TreeSet",
    search_mode="unordered",
):
    items: List[ProfileRuleXml] = element(tag="item")

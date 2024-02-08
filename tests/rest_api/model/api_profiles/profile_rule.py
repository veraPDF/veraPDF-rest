from typing import List

from pydantic import BaseModel
from pydantic_xml import BaseXmlModel, element

from tests.rest_api.model.api_profiles.rule_id import RuleID, RuleIDXml


class ProfileRule(BaseModel):
    object: str
    description: str
    test: str
    ruleId: RuleID


class ProfileRuleXml(
    BaseXmlModel,
    search_mode="unordered",
):
    object: str = element(tag="object")
    description: str = element(tag="description")
    ruleId: RuleIDXml


class ProfileRuleSXml(
    BaseXmlModel,
    tag="TreeSet",
    search_mode="unordered",
):
    items: List[ProfileRuleXml] = element(tag="item")

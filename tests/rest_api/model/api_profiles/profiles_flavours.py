from typing import List

from pydantic import BaseModel
from pydantic_xml import BaseXmlModel, element, RootXmlModel

from tests.rest_api.model.api_profiles.rule_id import RuleID, RuleIDXml


class ProfileFlavoursXMl(BaseXmlModel, tag="UnmodifiableSet"):
    item: List[str] = element(tag="item")

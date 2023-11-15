from typing import List

from pydantic import BaseModel

from tests.rest_api.model.api_profiles.rule_id import RuleID


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

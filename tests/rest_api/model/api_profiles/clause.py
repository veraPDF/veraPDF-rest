from typing import List

from pydantic import BaseModel

from tests.rest_api.model.api_profiles.profile_rule import ProfileRule


class Clause(BaseModel):
    clause: ProfileRule

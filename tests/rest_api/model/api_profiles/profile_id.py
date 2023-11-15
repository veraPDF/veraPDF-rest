from pydantic import BaseModel
from tests.rest_api.model.api_profiles.profile import Profile
from tests.rest_api.model.api_profiles.profile_variables import ProfileVariables
from tests.rest_api.model.api_profiles.profile_rule import ProfileRule


from typing import List, Optional


class ProfileID(BaseModel):
    details: Profile
    rules: List[ProfileRule] = []
    variables: List[ProfileVariables]
    tags: List[str] = []
    pdfaflavour: str
    hexSha1Digest: str

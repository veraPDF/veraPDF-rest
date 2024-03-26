from pydantic import BaseModel
from pydantic_xml import BaseXmlModel, element
from tests.rest_api.model.api_profiles.profile import Profile, ProfileXMl
from tests.rest_api.model.api_profiles.profile_variables import ProfileVariables
from tests.rest_api.model.api_profiles.profile_rule import ProfileRule


from typing import List


class ProfileID(BaseModel):
    details: Profile
    rules: List[ProfileRule]
    variables: List[ProfileVariables]
    tags: List[str]
    pdfaflavour: str
    hexSha1Digest: str


class ProfileIDXml(
    BaseXmlModel,
    tag="ValidationProfileImpl",
    search_mode="unordered",
):
    details: ProfileXMl
    pdfaflavour: str = element(tag="pdfaflavour")

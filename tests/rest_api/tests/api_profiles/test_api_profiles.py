import re
import requests
from tests.rest_api.tests.base_test import BaseClass
from tests.rest_api.model.api_profiles.profile import Profile

PROFILE_NAMES = [
    "PDF/A-1A validation profile",
    "PDF/UA-1 validation profile",
    "PDF/A-4E validation profile",
    "PDF/A-4F validation profile",
    "PDF/A-3U validation profile",
    "PDF/A-3A validation profile",
    "PDF/A-1B validation profile",
    "PDF/A-2U validation profile",
    "PDF/A-3B validation profile",
    "PDF/A-2B validation profile",
    "PDF/A-4 validation profile",
    "PDF/A-2A validation profile",
]

dateCreated_regex = re.compile(r"^[0-9]+$")
creator = "veraPDF Consortium"
description = re.compile(r".+")


def test_profiles_list_check():
    response = requests.get(BaseClass.ENDPOINT + "/api/profiles")
    assert response.status_code == 200

    profile_list = response.json()
    assert len(profile_list) == 12

    for prof_item in profile_list:
        profile_info = Profile(**prof_item)
        assert profile_info.name in PROFILE_NAMES
        assert profile_info.creator == creator
        assert description.match(profile_info.description)
        assert dateCreated_regex.match(str(profile_info.dateCreated))

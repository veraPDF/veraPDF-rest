import json
import xmltodict
import re

import requests

from tests.rest_api.model.api_profiles.profile import Profile, ProfileXMl, ProfilesXMl
from tests.conftest import get_base_url
from tests.rest_api.model.api_profiles.profile_rule import ProfileRuleXml

PROFILE_NAMES = sorted(
    (
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
    )
)
profiles_item_list = []
profiles_item_xml_list = []
dateCreated_regex = re.compile(r"^[0-9]+$")
creator = "veraPDF Consortium"
description = re.compile(r".+")


def test_profiles_list_check(get_base_url):
    response = requests.get(get_base_url + "/api/profiles")
    assert response.status_code == 200

    profile_list = response.json()

    for prof_item in profile_list:
        profile_info = Profile(**prof_item)
        profiles_item_list.append(profile_info.name)
        assert profile_info.creator == creator
        assert description.match(profile_info.description)
        assert dateCreated_regex.match(str(profile_info.dateCreated))
    profiles_item_list.sort()
    assert profiles_item_list == PROFILE_NAMES


def test_profiles_list_check_xml(get_base_url):
    url = get_base_url + "/api/profiles"
    headers = {"Accept": "application/xml"}

    response = requests.get(url, headers=headers)
    assert response.status_code == 200

    profiles_list_xml = response.text

    profile_list = ProfilesXMl.from_xml(profiles_list_xml)

    for item in profile_list.items:
        profiles_item_xml_list.append(item.name)
        assert item.creator == creator
        assert description.match(item.description)
        assert dateCreated_regex.match(str(item.dateCreated))
    profiles_item_xml_list.sort()
    assert profiles_item_xml_list == PROFILE_NAMES

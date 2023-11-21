import pytest
import requests

from tests.rest_api.model.api_profiles.profiles_flavours import ProfileFlavoursXMl
from tests.rest_api.tests.base_test import BaseClass
from tests.conftest import get_base_url
from tests.rest_api.model.api_profiles.profile_rule import (
    ProfileRule,
    ProfileRuleSXml,
)


def test_profile_flavours_check(get_base_url):
    response = requests.get(get_base_url + "/api/profiles/flavours")
    assert response.status_code == 200

    flavours_list = response.json()
    assert sorted(flavours_list) == BaseClass.FLAVOURS_LIST


def test_profile_flavours_xml_check(get_base_url):
    url = get_base_url + "/api/profiles/flavours"
    headers = {"Accept": "application/xml"}

    response = requests.get(url=url, headers=headers)
    assert response.status_code == 200

    flavours_xml = response.text
    flavours_list = ProfileFlavoursXMl.from_xml(flavours_xml)
    assert sorted(flavours_list.item) == BaseClass.FLAVOURS_LIST

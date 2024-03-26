import requests

from tests.rest_api.model.api_profiles.profile_ids import ProfileIdsXMl
from tests.rest_api.tests.base_test import BaseClass


def test_profile_ids_check(get_base_url):
    response = requests.get(get_base_url + "/api/profiles/ids")
    assert response.status_code == 200

    profile_ids = response.json()

    assert sorted(profile_ids) == BaseClass.PROFILE_IDS


def test_profile_ids_xml_check(get_base_url):
    url = get_base_url + "/api/profiles/ids"
    headers = {"Accept": "application/xml"}

    response = requests.get(url, headers=headers)
    assert response.status_code == 200

    profile_ids_xml = response.text
    profile_ids_list = ProfileIdsXMl.from_xml(profile_ids_xml)

    assert sorted(profile_ids_list.item) == BaseClass.PROFILE_IDS

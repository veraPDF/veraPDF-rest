import requests

from tests.rest_api.model.api_sha1.sha1 import Sha1XMl


def test_sha1_null_check(get_base_url):
    response = requests.get(get_base_url + "/api/sha1/null")
    assert response.status_code == 200

    profile_ids = response.json()

    assert profile_ids["hexSHA1"] == "da39a3ee5e6b4b0d3255bfef95601890afd80709"
    assert profile_ids["length"] == 0


def test_sha1_null_xml_check(get_base_url):
    url = get_base_url + "/api/sha1/null"
    headers = {"Accept": "application/xml"}

    response = requests.get(url, headers=headers)
    assert response.status_code == 200

    sha1_null_xml = response.text

    sha1_null = Sha1XMl.from_xml(sha1_null_xml)

    assert sha1_null.hexSHA1 == "da39a3ee5e6b4b0d3255bfef95601890afd80709"
    assert sha1_null.length == 0

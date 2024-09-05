from io import StringIO

import pytest
import requests
import xmltodict
from lxml import etree

from tests.rest_api.tests.base_test import BaseClass


@pytest.mark.parametrize(
    "profile_id, expected_profile_name",
    BaseClass.VALIDATION_LIST,
)
def test_api_validate_sha_profileid_check(
    profile_id, expected_profile_name, get_base_url
):
    headers = {
        "accept": "application/json",
        # requests won't add a boundary if this header is set when you pass files=
        # 'Content-Type': 'multipart/form-data',
    }

    files = {
        "sha1Hex": (None, "bb0a429d5449548a50de3d5bd2636d4f918272c4"),
        "file": ("a.pdf", open(BaseClass.FILE_TO_PARSE, "rb"), "application/pdf"),
    }

    response = requests.post(
        get_base_url + "/api/validate/" + profile_id, headers=headers, files=files
    )

    assert response.status_code == 200

    sha1_info = response.json()

    assert sha1_info["report"]["jobs"][0]["itemDetails"]["name"] == "a.pdf"
    assert (
        sha1_info["report"]["jobs"][0]["validationResult"][0]["profileName"]
        == expected_profile_name
    )


@pytest.mark.parametrize(
    "profile_id, expected_profile_name",
    BaseClass.VALIDATION_LIST,
)
def test_api_validate_sha_profileid_xml_check(
    profile_id, expected_profile_name, get_base_url
):
    headers = {
        "accept": "application/xml",
        # requests won't add a boundary if this header is set when you pass files=
        # 'Content-Type': 'multipart/form-data',
    }

    files = {
        "sha1Hex": (None, "bb0a429d5449548a50de3d5bd2636d4f918272c4"),
        "file": ("a.pdf", open(BaseClass.FILE_TO_PARSE, "rb"), "application/pdf"),
    }

    response = requests.post(
        get_base_url + "/api/validate/" + profile_id, headers=headers, files=files
    )

    assert response.status_code == 200

    details = xmltodict.parse(response.text)

    assert details["report"]["jobs"]["job"]["item"]["name"] == "a.pdf"
    assert (
        details["report"]["jobs"]["job"]["validationReport"]["@profileName"]
        == expected_profile_name
    )


@pytest.mark.parametrize(
    "profile_id, expected_profile_name",
    BaseClass.VALIDATION_LIST,
)
def test_api_validate_sha_profileid_html_check(
    profile_id, expected_profile_name, get_base_url
):
    url = get_base_url + "/api/validate/" + profile_id
    headers = {
        "accept": "text/html",
        # requests won't add a boundary if this header is set when you pass files=
        # 'Content-Type': 'multipart/form-data',
    }

    files = {
        "sha1Hex": (None, "bb0a429d5449548a50de3d5bd2636d4f918272c4"),
        "file": ("a.pdf", open(BaseClass.FILE_TO_PARSE, "rb"), "application/pdf"),
    }

    parser = etree.HTMLParser()

    page = requests.post(url, headers=headers, files=files)

    assert page.status_code == 200

    html = page.content.decode("utf-8")
    tree = etree.parse(StringIO(html), parser=parser)

    file = tree.xpath('//*[@id="table1"]//tr[1]//td[2]/text()')[0]
    profile = tree.xpath('//*[@id="table1"]//tr[2]//td[2]/text()')[0]

    assert file == "a.pdf"
    assert profile == expected_profile_name

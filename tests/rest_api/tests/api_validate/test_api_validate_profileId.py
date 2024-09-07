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
def test_api_validate_details_check(profile_id, expected_profile_name, get_base_url):
    url = get_base_url + "/api/validate/" + profile_id
    headers = {
        "accept": "application/json",
        # requests won't add a boundary if this header is set when you pass files=
        # 'Content-Type': 'multipart/form-data',
    }

    files = {
        "file": ("a.pdf", open(BaseClass.FILE_TO_PARSE, "rb"), "application/pdf"),
    }

    response = requests.post(url, headers=headers, files=files)

    details = response.json()

    assert "a.pdf" in (details["report"]["jobs"][0]["itemDetails"]["name"])
    assert (
        details["report"]["jobs"][0]["validationResult"][0]["profileName"]
        == expected_profile_name
    )


@pytest.mark.parametrize(
    "profile_id, expected_profile_name",
    BaseClass.VALIDATION_LIST,
)
def test_api_validate_details_xml_check(
    profile_id, expected_profile_name, get_base_url
):
    url = get_base_url + "/api/validate/" + profile_id
    headers = {
        "accept": "application/xml",
        # requests won't add a boundary if this header is set when you pass files=
        # 'Content-Type': 'multipart/form-data',
    }

    files = {
        "file": ("a.pdf", open(BaseClass.FILE_TO_PARSE, "rb"), "application/pdf"),
    }

    response = requests.post(url, headers=headers, files=files)

    details = xmltodict.parse(response.text)

    assert (
        details["report"]["jobs"]["job"]["validationReport"]["@profileName"]
        == expected_profile_name
    )


@pytest.mark.parametrize(
    "profile_id, expected_profile_name",
    BaseClass.VALIDATION_LIST,
)
def test_api_validate_details_html_check(
    profile_id, expected_profile_name, get_base_url
):
    url = get_base_url + "/api/validate/" + profile_id
    headers = {
        "accept": "text/html",
        # requests won't add a boundary if this header is set when you pass files=
        # 'Content-Type': 'multipart/form-data',
    }

    files = {
        "file": ("a.pdf", open(BaseClass.FILE_TO_PARSE, "rb"), "application/pdf"),
    }
    parser = etree.HTMLParser()

    page = requests.post(url, headers=headers, files=files)

    html = page.content.decode("utf-8")
    tree = etree.parse(StringIO(html), parser=parser)
    file = tree.xpath('//*[@id="table1"]//tr[1]//td[2]/text()')[0]
    profile = tree.xpath('//*[@id="table1"]//tr[2]//td[2]/a[1]/text()')[0]

    assert file == "a.pdf"
    assert profile == expected_profile_name

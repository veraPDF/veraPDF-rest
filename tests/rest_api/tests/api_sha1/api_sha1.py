import requests

from tests.conftest import get_base_url
from tests.rest_api.model.api_sha1.sha1 import Sha1XMl


def test_sha1_check(get_base_url):
    headers = {
        "accept": "application/json",
        # requests won't add a boundary if this header is set when you pass files=
        # 'Content-Type': 'multipart/form-data',
    }
    files = {
        "file": (
            "a.pdf",
            open("..//../../../tests/Resources/a.pdf", "rb"),
            "application/pdf",
        ),
    }

    response = requests.post(
        "https://demo.verapdf.org/api/sha1", headers=headers, files=files
    )

    sha1_info = response.json()

    assert sha1_info["hexSHA1"] == "bb0a429d5449548a50de3d5bd2636d4f918272c4"
    assert sha1_info["length"] == 3175


def test_sha1_xml_check(get_base_url):
    headers = {
        "accept": "application/xml",
        # requests won't add a boundary if this header is set when you pass files=
        # 'Content-Type': 'multipart/form-data',
    }
    files = {
        "file": (
            "a.pdf",
            open("..//../../../tests/Resources/a.pdf", "rb"),
            "application/pdf",
        ),
    }

    response = requests.post(
        "https://demo.verapdf.org/api/sha1", headers=headers, files=files
    )

    sha1_info_xml = response.text

    sha1_info = Sha1XMl.from_xml(sha1_info_xml)

    assert sha1_info.hexSHA1 == "bb0a429d5449548a50de3d5bd2636d4f918272c4"
    assert sha1_info.length == 3175

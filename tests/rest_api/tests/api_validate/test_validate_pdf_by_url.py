import pytest
import requests

from tests.rest_api.tests.base_test import BaseClass


@pytest.mark.parametrize(
    "test_url, expected_code, expected_message",
    [
        ("", 400, '"message":"URL is empty"'),
        ("https://abc.qwerty.com", 400, "URL is incorrect: https://abc.qwerty.com"),
        (
            "https://github.com/veraPDF/veraPDF-regression-tests/blob/integration/CLI/Resources/a.pdf",
            200,
            'id="verapdf-rest"',
        ),
    ],
)
def test_validate_pdf_with_url(test_url, expected_code, expected_message):
    url = BaseClass.ENDPOINT + "/api/validate/url/1b"
    files = {"url": test_url}
    headers = {}

    response = requests.post(url, headers=headers, files=files)
    assert response.status_code == expected_code
    assert expected_message in response.text

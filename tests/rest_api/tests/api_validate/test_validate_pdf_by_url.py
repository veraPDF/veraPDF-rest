import pytest
import requests


@pytest.mark.parametrize(
    "file_url, expected_code, expected_message",
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
def test_validate_pdf_with_url(file_url, expected_code, expected_message, get_base_url):
    url = get_base_url + "/api/validate/url/1b"
    files = {"url": file_url}
    headers = {}

    response = requests.post(url, headers=headers, files=files)
    assert response.status_code == expected_code
    assert expected_message in response.text

import pytest
import requests

from tests.conftest import get_base_url
from tests.rest_api.model.api_validate_details.result import Report, Result
from tests.rest_api.tests.base_test import BaseClass


@pytest.mark.parametrize(
    "file_url, expected_code, json_key, expected_message",
    [
        ("", 400, "message", "URL is empty"),
        (
            "https://abc.qwerty.com",
            400,
            "message",
            "URL is incorrect: https://abc.qwerty.com",
        ),
    ],
)
@pytest.mark.parametrize(
    "profile_id",
    BaseClass.PROFILE_IDS,
)
def test_validate_pdf_with_fail_url(
    file_url, expected_code, json_key, expected_message, profile_id, get_base_url
):
    url = get_base_url + "/api/validate/url/" + profile_id

    headers = {
        "accept": "application/json",
        # requests won't add a boundary if this header is set when you pass files=
        # 'Content-Type': 'multipart/form-data',
    }

    files = {
        "url": (None, file_url),
    }

    response = requests.post(url=url, headers=headers, files=files)

    assert response.status_code == expected_code
    assert expected_message == response.json()[json_key]


@pytest.mark.parametrize(
    "file_url, expected_code",
    [
        (
            "https://github.com/veraPDF/veraPDF-regression-tests/blob/integration/CLI/Resources/a.pdf",
            200,
        ),
    ],
)
@pytest.mark.parametrize(
    "profile_id",
    BaseClass.PROFILE_IDS,
)
def test_validate_pdf_with_pass_url(file_url, expected_code, profile_id, get_base_url):
    url = get_base_url + "/api/validate/url/" + profile_id

    headers = {
        "accept": "application/json",
        # requests won't add a boundary if this header is set when you pass files=
        # 'Content-Type': 'multipart/form-data',
    }

    files = {
        "url": (None, file_url),
    }

    response = requests.post(url=url, headers=headers, files=files)

    result = Result(**response.json())
    assert result.report.batchSummary.totalJobs == 1
    assert result.report.jobs[0].logs.logs[0].level == "WARNING"
    assert result.report.jobs[0].logs.logs[0].occurrences == 1
    assert "a.pdf" in result.report.jobs[0].logs.logs[0].message

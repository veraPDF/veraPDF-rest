import re

import requests

from tests.rest_api.model.api_validate_details.api_validate_details import (
    ApiValidateDetails,
)

details_version = re.compile(r"\d.\d\d.\d+")


def test_api_validate_details_check(get_base_url):
    response = requests.get(get_base_url + "/api/validate/details")
    assert response.status_code == 200

    details = ApiValidateDetails(**(response.json()))

    assert details.id == "http://pdfa.verapdf.org/foundry#verapdf"
    assert details.name == "VeraPDF Foundry"
    assert details_version.match(details.version)
    assert details.provider == "The veraPDF Consortium."
    assert details.description == "veraPDF greenfield foundry instance."

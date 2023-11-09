import re
import requests

from tests.rest_api.tests.base_test import BaseClass
from tests.rest_api.model.api_info.api_endpoint import ApiEndpoint

buildDate_regex = re.compile(r"^[0-9]+$")
version_regex = re.compile(r"^[0-9]\.[0-9]\.[0-9]-SNAPSHOT")


def test_api_check():
    response = requests.get(BaseClass.ENDPOINT + "/api")
    assert response.status_code == 200
    resp = response.json()

    api = ApiEndpoint(**resp)

    assert api.id == "verapdf-rest"
    assert version_regex.match(api.version)
    assert buildDate_regex.match(str(api.buildDate))

import re
import requests

from tests.rest_api.tests.base_test import BaseClass
from tests.rest_api.model.api_info.api_info import ApiInfo

os_name = re.compile(r".+")
server_machAddress = re.compile(r"..-..-..-..-..-..")


def test_api_info_check():
    response = requests.get(BaseClass.ENDPOINT + "/api/info")
    assert response.status_code == 200
    resp = response.json()

    api_info = ApiInfo(**resp)

    assert api_info.java.architecture == "x64"
    assert os_name.match(api_info.os.name)
    assert server_machAddress.match(api_info.server.machAddress)

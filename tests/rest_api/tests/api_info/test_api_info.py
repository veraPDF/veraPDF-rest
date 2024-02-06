import re
import requests
from tests.rest_api.model.api_info.environment import Environment
from tests.rest_api.model.api_info.environment import EnvironmentXml


os_name = re.compile(r".+")
server_machAddress = re.compile(r"..-..-..-..-..-..")


def test_api_info_check(get_base_url):
    response = requests.get(get_base_url + "/api/info")
    assert response.status_code == 200
    resp = response.json()

    api_info = Environment(**resp)

    assert api_info.java.architecture == "x64"
    assert os_name.match(api_info.os.name)
    assert server_machAddress.match(api_info.server.machAddress)


def test_api_info_xml_check(get_base_url):
    url = get_base_url + "/api/info"
    headers = {"Accept": "application/xml"}

    response = requests.get(url=url, headers=headers)
    assert response.status_code == 200

    api_info = EnvironmentXml.from_xml(response.text)

    api_info.java.architecture == "x64"
    assert os_name.match(api_info.os.name)
    assert server_machAddress.match(api_info.server.machAddress)

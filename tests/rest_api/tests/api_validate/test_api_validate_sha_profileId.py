import pytest
import requests


@pytest.mark.skip(reason="Error: response status is 404")
def test_api_validate_sha_profileid_check():
    headers = {
        'accept': 'application/xml',
        # requests won't add a boundary if this header is set when you pass files=
        # 'Content-Type': 'multipart/form-data',
    }

    files = {
        'file': ('a.pdf', open('../../../Resources/a.pdf', 'rb'), 'application/pdf'),
    }

    response = requests.post('https://demo.verapdf.org/api/validate/sha/1b', headers=headers, files=files)
    assert response.status_code == 200


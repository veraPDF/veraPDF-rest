import pytest
import requests

from tests.rest_api.model.api_profiles.profile_id import ProfileID, ProfileIDXml
from tests.conftest import get_base_url
from tests.rest_api.tests.base_test import BaseClass


class TestProfileId(BaseClass):
    @pytest.mark.parametrize(
        "profile_id, expected_pdfaflavour,  expected_name",
        BaseClass.PROFILE_LIST,
    )
    def test_profile_id_check(
        self, profile_id, expected_pdfaflavour, expected_name, get_base_url
    ):
        response = requests.get(get_base_url + "/api/profiles/" + profile_id)
        assert response.status_code == 200

        profile_id = response.json()
        profile_id_info = ProfileID(**profile_id)

        assert profile_id_info.details.name == expected_name
        assert profile_id_info.details.creator == self.CREATOR
        assert profile_id_info.pdfaflavour == expected_pdfaflavour

    @pytest.mark.parametrize(
        "profile_id, expected_pdfaflavour,  expected_name",
        BaseClass.PROFILE_LIST,
    )
    def test_profile_id_xml_check(
        self, profile_id, expected_pdfaflavour, expected_name, get_base_url
    ):
        url = get_base_url + "/api/profiles/" + profile_id
        headers = {"Accept": "application/xml"}

        response = requests.get(url, headers=headers)
        assert response.status_code == 200

        profile_id_xml = response.text
        profile_id = ProfileIDXml.from_xml(profile_id_xml)

        assert profile_id.pdfaflavour == expected_pdfaflavour
        assert profile_id.details.creator == self.CREATOR

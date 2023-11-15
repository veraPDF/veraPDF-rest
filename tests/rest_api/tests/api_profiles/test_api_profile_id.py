import pytest
import requests

from tests.rest_api.model.api_profiles.profile_id import ProfileID

creator = "veraPDF Consortium"


@pytest.mark.parametrize(
    # 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f or ua1
    "profile_id, expected_pdfaflavour,  expected_name",
    [
        ("1b", "PDFA_1_B", "PDF/A-1B validation profile"),
        ("1a", "PDFA_1_A", "PDF/A-1A validation profile"),
        ("2b", "PDFA_2_B", "PDF/A-2B validation profile"),
        ("2a", "PDFA_2_A", "PDF/A-2A validation profile"),
        ("2u", "PDFA_2_U", "PDF/A-2U validation profile"),
        ("3a", "PDFA_3_A", "PDF/A-3A validation profile"),
        ("3b", "PDFA_3_B", "PDF/A-3B validation profile"),
        ("3u", "PDFA_3_U", "PDF/A-3U validation profile"),
        ("4", "PDFA_4", "PDF/A-4 validation profile"),
        ("4f", "PDFA_4_F", "PDF/A-4F validation profile"),
        ("ua1", "PDFUA_1", "PDF/UA-1 validation profile"),
    ],
)
def test_profile_id_check(
    profile_id, expected_pdfaflavour, expected_name, get_base_url
):
    response = requests.get(get_base_url + "/api/profiles/" + profile_id)
    assert response.status_code == 200

    profile_id = response.json()
    profile_id_info = ProfileID(**profile_id)

    assert profile_id_info.details.name == expected_name
    assert profile_id_info.details.creator == creator
    assert profile_id_info.pdfaflavour == expected_pdfaflavour

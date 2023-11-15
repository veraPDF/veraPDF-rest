import pytest
import requests

from tests.rest_api.model.api_profiles.profile_rule import ProfileRule


@pytest.mark.parametrize(
    # 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f or ua1
    "profile_id, expected_clause",
    [
        ("1b", "6.7.3"),
        ("1a", "6.1.7"),
        ("2b", "6.2.11.3.1"),
        ("2a", "6.7.3.4"),
        ("2u", "6.2.11.3.1"),
        ("3a", "6.2.11.3.2"),
        ("3b", "6.2.11.3.2"),
        ("3u", "6.3.1"),
        ("4", "6.6.1"),
        ("4e", "6.1.6.2"),
        ("4f", "6.2.4.2"),
        ("ua1", "7.18.8"),
    ],
)
def test_profile_id_clause_check(profile_id, expected_clause, get_base_url):
    response = requests.get(
        get_base_url + "/api/profiles/" + profile_id + "/" + expected_clause
    )
    assert response.status_code == 200

    clause_list = response.json()
    clause_info = ProfileRule(**clause_list[0])

    assert clause_info.ruleId.clause == expected_clause
    assert clause_info.ruleId.testNumber == 1

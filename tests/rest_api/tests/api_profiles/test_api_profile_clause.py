import pytest
import requests

from tests.conftest import get_base_url
from tests.rest_api.model.api_profiles.profile_rule import (
    ProfileRule,
    ProfileRuleSXml,
)

test_data = (
    # 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f or ua1
    ("1b", "6.7.3", 8),
    ("1a", "6.1.7", 3),
    ("2b", "6.2.11.3.1", 1),
    ("2a", "6.7.3.4", 2),
    ("2u", "6.2.11.3.1", 1),
    ("3a", "6.2.11.3.2", 1),
    ("3b", "6.2.11.3.2", 1),
    ("3u", "6.3.1", 1),
    ("4", "6.6.1", 2),
    ("4e", "6.1.6.2", 1),
    ("4f", "6.2.4.2", 3),
    ("ua1", "7.18.8", 1),
)


@pytest.mark.parametrize(
    "profile_id, expected_clause, count_items",
    test_data,
)
def test_profile_id_clause_check(
    profile_id, expected_clause, count_items, get_base_url
):
    response = requests.get(
        get_base_url + "/api/profiles/" + profile_id + "/" + expected_clause
    )
    assert response.status_code == 200

    clause_list = response.json()
    clause_info = ProfileRule(**clause_list[0])

    assert clause_info.ruleId.clause == expected_clause
    assert clause_info.ruleId.testNumber == 1


@pytest.mark.parametrize(
    "profile_id, expected_clause, count_items",
    test_data,
)
def test_profile_id_clause_xml_check(
    profile_id, expected_clause, count_items, get_base_url
):
    url = get_base_url + "/api/profiles/" + profile_id + "/" + expected_clause
    headers = {"Accept": "application/xml"}

    response = requests.get(url=url, headers=headers)
    assert response.status_code == 200

    clause_list_xml = response.text
    clause_list = ProfileRuleSXml.from_xml(clause_list_xml)

    assert len(clause_list.items) == count_items
    assert clause_list.items[0].ruleId.clause == expected_clause

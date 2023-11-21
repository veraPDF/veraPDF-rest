import pytest
import requests

from tests.rest_api.tests.base_test import BaseClass
from tests.conftest import get_base_url
from tests.rest_api.model.api_profiles.profile_rule import (
    ProfileRule,
    ProfileRuleSXml,
)


@pytest.mark.parametrize(
    "profile_id, expected_clause, expected_clause_items",
    BaseClass.PROFILE_CLAUSE,
)
def test_profile_id_clause_check(
    profile_id, expected_clause, expected_clause_items, get_base_url
):
    response = requests.get(
        get_base_url + "/api/profiles/" + profile_id + "/" + expected_clause
    )
    assert response.status_code == 200

    clause_list = response.json()
    item_list = []

    for item in clause_list:
        item_list.append(ProfileRule(**item))

    assert len(item_list) == expected_clause_items

    for item in item_list:
        assert item.ruleId.clause == expected_clause


@pytest.mark.parametrize(
    "profile_id, expected_clause, expected_clause_items",
    BaseClass.PROFILE_CLAUSE,
)
def test_profile_id_clause_xml_check(
    profile_id, expected_clause, expected_clause_items, get_base_url
):
    url = get_base_url + "/api/profiles/" + profile_id + "/" + expected_clause
    headers = {"Accept": "application/xml"}

    response = requests.get(url=url, headers=headers)
    assert response.status_code == 200

    clause_list_xml = response.text
    clause_list = ProfileRuleSXml.from_xml(clause_list_xml)

    assert len(clause_list.items) == expected_clause_items

    for item in clause_list.items:
        assert item.ruleId.clause == expected_clause

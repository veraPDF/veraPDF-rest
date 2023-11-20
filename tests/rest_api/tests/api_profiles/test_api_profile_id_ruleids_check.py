import pytest
import requests

from tests.rest_api.model.api_profiles.profile_id import ProfileID, ProfileIDXml
from tests.conftest import get_base_url
from tests.rest_api.model.api_profiles.rule_id import RuleID, RulesXml
from tests.rest_api.tests.base_test import BaseClass


class TestProfileRuleids(BaseClass):
    @pytest.mark.parametrize(
        "profile_id, expected_count_rules",
        BaseClass.PROFILE_RULES,
    )
    def test_ruleids_check(self, profile_id, expected_count_rules, get_base_url):
        response = requests.get(
            get_base_url + "/api/profiles/" + profile_id + "/ruleids"
        )
        assert response.status_code == 200
        item_list = []

        rules = response.json()

        for item in rules:
            item_list.append(RuleID(**item))

        assert len(item_list) == expected_count_rules

    @pytest.mark.parametrize(
        "profile_id, expected_count_rules",
        BaseClass.PROFILE_RULES,
    )
    def test_ruleids_xml_check(self, profile_id, expected_count_rules, get_base_url):
        url = get_base_url + "/api/profiles/" + profile_id + "/ruleids"
        headers = {"Accept": "application/xml"}

        response = requests.get(url, headers=headers)
        assert response.status_code == 200

        rules_xml = response.text

        rules_info = RulesXml.from_xml(rules_xml)
        assert len(rules_info.items) == expected_count_rules

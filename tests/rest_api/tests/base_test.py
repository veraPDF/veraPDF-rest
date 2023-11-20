""" Possible urls:
https://demo.verapdf.org/swagger
https://dev.verapdf-rest.duallab.com/swagger
http://localhost:8080/api
"""
import itertools


class BaseClass:
    CREATOR = "veraPDF Consortium"
    PROFILE_GENERAL = (
        # 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f or ua1
        # profile_id, pdfaflavour, profile_name, profile_rule_count
        ("1b", "PDFA_1_B", "PDF/A-1B validation profile", 127),
        ("1a", "PDFA_1_A", "PDF/A-1A validation profile", 133),
        ("2b", "PDFA_2_B", "PDF/A-2B validation profile", 143),
        ("2a", "PDFA_2_A", "PDF/A-2A validation profile", 151),
        ("2u", "PDFA_2_U", "PDF/A-2U validation profile", 145),
        ("3a", "PDFA_3_A", "PDF/A-3A validation profile", 153),
        ("3b", "PDFA_3_B", "PDF/A-3B validation profile", 145),
        ("3u", "PDFA_3_U", "PDF/A-3U validation profile", 147),
        ("4", "PDFA_4", "PDF/A-4 validation profile", 108),
        ("4e", "PDFA_4_E", "PDF/A-4E validation profile", 108),
        ("4f", "PDFA_4_F", "PDF/A-4F validation profile", 108),
        ("ua1", "PDFUA_1", "PDF/UA-1 validation profile", 103),
    )
    PROFILE_LIST = [
        list(itertools.compress(item, [1, 1, 1, 0])) for item in PROFILE_GENERAL
    ]
    PROFILE_IDS = [
        list(itertools.compress(item, [1, 0, 0, 0])) for item in PROFILE_GENERAL
    ]
    PROFILE_NAMES = sorted(
        [
            item[0]
            for item in [
                list(itertools.compress(item, [0, 0, 1, 0])) for item in PROFILE_GENERAL
            ]
        ]
    )
    PROFILE_RULES = [
        list(itertools.compress(item, [1, 0, 0, 1])) for item in PROFILE_GENERAL
    ]

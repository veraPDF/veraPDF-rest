""" Possible urls:
https://demo.verapdf.org/swagger
https://dev.verapdf-rest.duallab.com/swagger
http://localhost:8080/api
"""
import itertools


class BaseClass:
    CREATOR = "veraPDF Consortium"
    FILE_TO_PARSE = "./tests/Resources/a.pdf"
    PROFILE_GENERAL = (
        # 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f, ua1 or ua2
        # profile id, pdfa flavour, profile name, profile rule count, clause & clause items
        ("1b", "PDFA_1_B", "PDF/A-1B validation profile", 128, ("6.7.3", 8)),
        ("1a", "PDFA_1_A", "PDF/A-1A validation profile", 134, ("6.1.7", 3)),
        ("2b", "PDFA_2_B", "PDF/A-2B validation profile", 143, ("6.2.11.3.1", 1)),
        ("2a", "PDFA_2_A", "PDF/A-2A validation profile", 152, ("6.7.3.4", 3)),
        ("2u", "PDFA_2_U", "PDF/A-2U validation profile", 145, ("6.2.11.3.1", 1)),
        ("3a", "PDFA_3_A", "PDF/A-3A validation profile", 154, ("6.2.11.3.2", 1)),
        ("3b", "PDFA_3_B", "PDF/A-3B validation profile", 145, ("6.2.11.3.2", 1)),
        ("3u", "PDFA_3_U", "PDF/A-3U validation profile", 147, ("6.3.1", 1)),
        ("4", "PDFA_4", "PDF/A-4 validation profile", 108, ("6.6.1", 2)),
        ("4e", "PDFA_4_E", "PDF/A-4E validation profile", 108, ("6.1.6.2", 1)),
        ("4f", "PDFA_4_F", "PDF/A-4F validation profile", 108, ("6.2.4.2", 3)),
        ("ua1", "PDFUA_1", "PDF/UA-1 validation profile", 104, ("7.18.8", 1)),
        ("ua2", "PDFUA_2", "PDF/UA-2 + Tagged PDF validation profile", 1750, ("5", 5)),
    )

    FLAVOURS_LIST = sorted(
        [
            item[0]
            for item in [
                list(itertools.compress(item, [0, 1, 0, 0, 0]))
                for item in PROFILE_GENERAL
            ]
        ]
    )
    VALIDATION_LIST = [
        list(itertools.compress(item, [1, 0, 1, 0, 0])) for item in PROFILE_GENERAL
    ]

    PROFILE_LIST = [
        list(itertools.compress(item, [1, 1, 1, 0, 0])) for item in PROFILE_GENERAL
    ]
    PROFILE_IDS = sorted(
        [
            item[0]
            for item in [
                list(itertools.compress(item, [1, 0, 0, 0, 0]))
                for item in PROFILE_GENERAL
            ]
        ]
    )
    PROFILE_NAMES = sorted(
        [
            item[0]
            for item in [
                list(itertools.compress(item, [0, 0, 1, 0, 0]))
                for item in PROFILE_GENERAL
            ]
        ]
    )
    PROFILE_RULES = [
        list(itertools.compress(item, [1, 0, 0, 1, 0])) for item in PROFILE_GENERAL
    ]

    PROFILE_CLAUSE = []
    for i in list(
        ([list(itertools.compress(item, [1, 0, 0, 0, 1])) for item in PROFILE_GENERAL])
    ):
        x, (y, z) = i
        PROFILE_CLAUSE.append([x, y, z])

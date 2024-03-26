import pytest


def pytest_addoption(parser):
    parser.addoption(
        "--base_url", action="store", default="https://dev.verapdf-rest.duallab.com/"
    )


@pytest.fixture(scope="session")
def get_base_url(request):
    url = request.config.getoption(
        "--base_url", default="https://dev.verapdf-rest.duallab.com/"
    )
    print("\n\n#### Base_url ...", url)
    return url

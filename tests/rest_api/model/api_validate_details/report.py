from typing import List

from pydantic_xml import BaseXmlModel, element

from tests.rest_api.model.api_validate_details.job import JobXml, JobsXml


class ReportXml(
    BaseXmlModel,
    search_mode="unordered",
):
    pass
    # jobs: JobsXml = element(tag='jobs')

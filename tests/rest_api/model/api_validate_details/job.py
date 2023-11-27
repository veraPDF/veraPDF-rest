from typing import List, Dict

from pydantic import BaseModel
from pydantic_xml import BaseXmlModel, element

from tests.rest_api.model.api_validate_details.log import Logs


class JobXml(
    BaseXmlModel,
    tag="job",
    search_mode="unordered",
):
    validationReport: Dict[str, str] = element(tag="validationReport")


class JobsXml(
    BaseXmlModel,
    tag="jobs",
    search_mode="unordered",
):
    jobs: JobXml


class Job(BaseModel):
    # itemDetails: ItemDetails
    # taskException:
    # processingTime:
    logs: Logs

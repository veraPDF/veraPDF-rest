from typing import List, Dict

from pydantic_xml import BaseXmlModel, element


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

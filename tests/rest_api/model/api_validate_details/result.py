from pydantic import BaseModel

from tests.rest_api.model.api_validate_details.report import Report


class Result(BaseModel):
    report: Report

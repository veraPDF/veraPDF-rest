from typing import List

from pydantic import BaseModel

from tests.rest_api.model.api_validate_details.summary import BatchSummary
from tests.rest_api.model.api_validate_details.job import Job


class Report(BaseModel):
    batchSummary: BatchSummary
    jobs: List[Job]

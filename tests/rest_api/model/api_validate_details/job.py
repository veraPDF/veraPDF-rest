from pydantic import BaseModel

from tests.rest_api.model.api_validate_details.log import Logs


class Job(BaseModel):
    logs: Logs

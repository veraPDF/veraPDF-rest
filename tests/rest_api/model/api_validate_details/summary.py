from pydantic import BaseModel


class ValidationSummary(BaseModel):
    totalJobCount: int


class BatchSummary(BaseModel):
    totalJobs: int
    multiJob: bool
    validationSummary: ValidationSummary

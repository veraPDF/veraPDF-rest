from typing import List

from pydantic_xml import BaseXmlModel, element


class ProfileIdsXMl(BaseXmlModel, tag="UnmodifiableSet"):
    item: List[str] = element(tag="item")

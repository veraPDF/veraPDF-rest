from typing import List

from pydantic_xml import BaseXmlModel, element


class ProfileFlavoursXMl(BaseXmlModel, tag="UnmodifiableSet"):
    item: List[str] = element(tag="item")

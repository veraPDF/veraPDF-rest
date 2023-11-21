from pydantic_xml import BaseXmlModel, element


class Sha1XMl(BaseXmlModel, tag="ByteStreamIdImpl"):
    hexSHA1: str = element(tag="hexSHA1")
    length: int = element(tag="length")

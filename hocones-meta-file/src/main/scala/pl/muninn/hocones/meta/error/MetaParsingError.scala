package pl.muninn.hocones.meta.error

case class MetaParsingError(message: String) extends Error(message)

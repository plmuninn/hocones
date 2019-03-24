package pl.onewebpro.hocones.parser

case class ParsingError(message: String, cause: Throwable = None.orNull) extends Error(message, cause)

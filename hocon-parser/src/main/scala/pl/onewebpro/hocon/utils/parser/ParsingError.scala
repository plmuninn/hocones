package pl.onewebpro.hocon.utils.parser

case class ParsingError(message:String, cause:Throwable = None.orNull) extends Error

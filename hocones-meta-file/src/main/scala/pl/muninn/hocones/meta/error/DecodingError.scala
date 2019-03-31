package pl.muninn.hocones.meta.error

case class DecodingError(message: String, cause: Throwable = None.orNull) extends Error(message, cause)

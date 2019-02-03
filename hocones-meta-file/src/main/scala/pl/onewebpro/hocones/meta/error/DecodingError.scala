package pl.onewebpro.hocones.meta.error

case class DecodingError(message: String, cause: Throwable = None.orNull) extends Error

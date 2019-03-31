package pl.muninn.hocones.meta.error

case class MetaError(message: String, cause: Throwable = None.orNull) extends Error(message, cause)

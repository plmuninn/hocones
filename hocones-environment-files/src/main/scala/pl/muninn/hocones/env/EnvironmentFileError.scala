package pl.muninn.hocones.env

case class EnvironmentFileError(message: String, cause: Throwable = None.orNull) extends Error(message, cause)

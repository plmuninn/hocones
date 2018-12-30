package pl.onewebpro.hocones.env

case class EnvironmentFileError(message: String, cause: Throwable = None.orNull)
    extends Error

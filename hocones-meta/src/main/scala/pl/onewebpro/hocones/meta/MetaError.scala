package pl.onewebpro.hocones.meta

case class MetaError(message: String, cause: Throwable = None.orNull)
    extends Error

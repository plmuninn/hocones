package pl.onewebpro.hocones.meta.document

case class MarkdownDocumentationError(message: String, cause: Throwable = None.orNull) extends Error

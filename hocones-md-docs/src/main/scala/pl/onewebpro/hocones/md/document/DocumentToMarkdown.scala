package pl.onewebpro.hocones.md.document
import pl.muninn.scalamdtag.tags.Markdown
import pl.onewebpro.hocones.meta.document.model.Document

trait DocumentToMarkdown[T <: Document[_]] {
  def toMd(document: T): Markdown
}

object DocumentToMarkdown {
  def apply[A <: Document[_]](implicit instance: DocumentToMarkdown[A]): DocumentToMarkdown[A] = instance
}

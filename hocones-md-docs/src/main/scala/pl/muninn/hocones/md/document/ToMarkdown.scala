package pl.muninn.hocones.md.document
import pl.muninn.scalamdtag.tags.Markdown

trait ToMarkdown[T] {
  def toMd(document: T): Markdown
}

object ToMarkdown {
  def apply[A](implicit instance: ToMarkdown[A]): ToMarkdown[A] = instance

  implicit class ToMarkdownOps[T](value: T)(implicit instance: ToMarkdown[T]) {
    def toMd: Markdown = instance.toMd(value)
  }
}

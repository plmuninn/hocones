package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.MarkdownElement
import pl.onewebpro.hocones.md.document.model.Document

trait DocumentToMdGenerator[T <: Document[_]] {
  def toMd(document: T): MarkdownElement
}

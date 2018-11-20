package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.{Markdown, MarkdownElement}
import pl.onewebpro.hocones.md.document.model.ObjectDocument

class ObjectDocumentMarkdown extends DocumentToMdGenerator[ObjectDocument] {
  override def toMd(document: ObjectDocument): MarkdownElement = Markdown.text("Object document")
}

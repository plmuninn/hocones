package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.{Markdown, MarkdownElement}
import pl.onewebpro.hocones.md.document.model.ArrayDocument

class ArrayDocumentMarkdown extends DocumentToMdGenerator[ArrayDocument] {
  override def toMd(document: ArrayDocument): MarkdownElement = Markdown.text("Array document")
}

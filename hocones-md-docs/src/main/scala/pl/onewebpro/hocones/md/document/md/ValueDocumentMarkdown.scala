package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.{Markdown, MarkdownElement}
import pl.onewebpro.hocones.md.document.model.ValueDocument

class ValueDocumentMarkdown extends DocumentToMdGenerator[ValueDocument] {
  override def toMd(document: ValueDocument): MarkdownElement = Markdown.text("Value document")
}

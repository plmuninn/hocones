package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.{Markdown, MarkdownElement}
import pl.onewebpro.hocones.md.document.model.ConcatenationDocument

class ConcatenationDocumentMarkdown extends DocumentToMdGenerator[ConcatenationDocument] {
  override def toMd(document: ConcatenationDocument): MarkdownElement = Markdown.text("Concatenation document")
}

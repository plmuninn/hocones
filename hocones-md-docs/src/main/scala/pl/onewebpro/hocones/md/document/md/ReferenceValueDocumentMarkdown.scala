package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.{Markdown, MarkdownElement}
import pl.onewebpro.hocones.md.document.model.ReferenceValueDocument

class ReferenceValueDocumentMarkdown extends DocumentToMdGenerator[ReferenceValueDocument] {
  override def toMd(document: ReferenceValueDocument): MarkdownElement = Markdown.text("Reference value document")
}

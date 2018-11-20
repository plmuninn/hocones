package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.{Markdown, MarkdownElement}
import pl.onewebpro.hocones.md.document.model.ResolvedReferenceDocument

class ResolvedReferenceDocumentMarkdown extends DocumentToMdGenerator[ResolvedReferenceDocument] {
  override def toMd(document: ResolvedReferenceDocument): MarkdownElement = Markdown.text("Resolved document")
}

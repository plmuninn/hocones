package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.{Markdown, MarkdownElement}
import pl.onewebpro.hocones.md.document.model.MergedValuesDocument

class MergedValuesDocumentMarkdown extends DocumentToMdGenerator[MergedValuesDocument] {
  override def toMd(document: MergedValuesDocument): MarkdownElement = Markdown.text("Merged values document")
}

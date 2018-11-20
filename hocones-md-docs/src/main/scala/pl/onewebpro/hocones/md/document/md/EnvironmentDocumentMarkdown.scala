package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.{Markdown, MarkdownElement}
import pl.onewebpro.hocones.md.document.model.EnvironmentDocument

class EnvironmentDocumentMarkdown extends DocumentToMdGenerator[EnvironmentDocument] {
  override def toMd(document: EnvironmentDocument): MarkdownElement = Markdown.text("Environment document")
}

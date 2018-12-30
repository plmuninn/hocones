package pl.onewebpro.hocones.md.document.md.documents

import net.steppschuh.markdowngenerator.MarkdownElement
import pl.onewebpro.hocones.md.document.md.{
  DocumentToMdGenerator,
  HoconEnvironmentValueOps
}
import pl.onewebpro.hocones.md.document.model.EnvironmentDocument

class EnvironmentDocumentMarkdown
    extends DocumentToMdGenerator[EnvironmentDocument]
    with HoconEnvironmentValueOps {
  override def toMd(document: EnvironmentDocument): MarkdownElement =
    heading(document)
      .typeOfDocument(document)
      .name(document.value)
      .isOptional(document.value)
      .description(document)
      .metaInformation(document.metaInformation)
      .from(document.value)
      .toMarkdownElement
}

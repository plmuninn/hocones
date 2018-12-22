package pl.onewebpro.hocones.md.document.md.documents

import net.steppschuh.markdowngenerator.MarkdownElement
import pl.onewebpro.hocones.md.document.md.{
  DocumentToMdGenerator,
  HoconResultTypeOps
}
import pl.onewebpro.hocones.md.document.model.ObjectDocument

class ObjectDocumentMarkdown
    extends DocumentToMdGenerator[ObjectDocument]
    with HoconResultTypeOps {
  override def toMd(document: ObjectDocument): MarkdownElement =
    heading(document)
      .typeOfDocument(document)
      .size(document.value)
      .description(document)
      .environments(document.value)
      .references(document.value)
      .unresolvedReferences(document.value)
      .metaInformation(document.metaInformation)
      .from(document.value)
      .toMarkdownElement
}

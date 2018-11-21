package pl.onewebpro.hocones.md.document.md.documents

import net.steppschuh.markdowngenerator.MarkdownElement
import pl.onewebpro.hocones.md.document.md.{DocumentToMdGenerator, HoconReferenceOps}
import pl.onewebpro.hocones.md.document.model.ReferenceValueDocument

class ReferenceValueDocumentMarkdown
  extends DocumentToMdGenerator[ReferenceValueDocument]
    with HoconReferenceOps {

  override def toMd(document: ReferenceValueDocument): MarkdownElement =
    heading(document)
      .typeOfDocument(document)
      .referenceTo(document.value)
      .description(document)
      .metaInformation(document.metaInformation)
      .from(document.value)
      .toMarkdownElement
}

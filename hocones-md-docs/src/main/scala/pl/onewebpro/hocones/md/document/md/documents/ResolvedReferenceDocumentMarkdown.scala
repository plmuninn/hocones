package pl.onewebpro.hocones.md.document.md.documents

import net.steppschuh.markdowngenerator.MarkdownElement
import pl.onewebpro.hocones.md.document.md.{DocumentToMdGenerator, HoconResolvedReferenceOps}
import pl.onewebpro.hocones.md.document.model.ResolvedReferenceDocument

class ResolvedReferenceDocumentMarkdown
  extends DocumentToMdGenerator[ResolvedReferenceDocument]
  with HoconResolvedReferenceOps {

  override def toMd(document: ResolvedReferenceDocument): MarkdownElement =
    heading(document)
      .typeOfDocument(document)
      .referenceTo(document.value)
      .defaultValue(document.value)
      .description(document)
      .metaInformation(document.metaInformation)
      .toMarkdownElement
}

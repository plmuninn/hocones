package pl.onewebpro.hocones.md.document.md.documents

import net.steppschuh.markdowngenerator.MarkdownElement
import pl.onewebpro.hocones.md.document.md.{DocumentToMdGenerator, HoconReferenceOps, HoconResolvedReferenceOps}
import pl.onewebpro.hocones.md.document.model.ResolvedReferenceDocument

class ResolvedReferenceDocumentMarkdown
  extends DocumentToMdGenerator[ResolvedReferenceDocument]
    with HoconResolvedReferenceOps
    with HoconReferenceOps {

  override def toMd(document: ResolvedReferenceDocument): MarkdownElement =
    heading(document)
      .typeOfDocument(document)
      .referenceTo(document.value)
      .defaultValue(document.value)
      .description(document)
      .metaInformation(document.metaInformation)
      .from(document.value)
      .toMarkdownElement
}

package pl.onewebpro.hocones.md.document.md.documents

import net.steppschuh.markdowngenerator.MarkdownElement
import pl.onewebpro.hocones.md.document.md.{DocumentToMdGenerator, HoconConcatenationOps}
import pl.onewebpro.hocones.md.document.model.ConcatenationDocument

class ConcatenationDocumentMarkdown
  extends DocumentToMdGenerator[ConcatenationDocument]
    with HoconConcatenationOps {

  override def toMd(document: ConcatenationDocument): MarkdownElement =
    heading(document)
      .typeOfDocument(document)
      .pattern(document.value)
      .description(document)
      .environments(document.value)
      .references(document.value)
      .unresolvedReferences(document.value)
      .metaInformation(document.metaInformation)
      .toMarkdownElement
}

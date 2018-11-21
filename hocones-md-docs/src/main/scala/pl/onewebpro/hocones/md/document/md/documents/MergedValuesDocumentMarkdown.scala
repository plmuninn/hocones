package pl.onewebpro.hocones.md.document.md.documents

import net.steppschuh.markdowngenerator.MarkdownElement
import pl.onewebpro.hocones.md.document.md.DocumentToMdGenerator
import pl.onewebpro.hocones.md.document.model.MergedValuesDocument

class MergedValuesDocumentMarkdown extends DocumentToMdGenerator[MergedValuesDocument] {

  override def toMd(document: MergedValuesDocument): MarkdownElement =
    heading(document)
      .typeOfDocument(document)
      .description(document)
      .metaInformation(document.metaInformation)
      .toMarkdownElement
}

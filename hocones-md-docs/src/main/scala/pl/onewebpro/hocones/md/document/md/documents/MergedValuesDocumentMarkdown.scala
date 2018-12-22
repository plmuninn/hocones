package pl.onewebpro.hocones.md.document.md.documents

import net.steppschuh.markdowngenerator.MarkdownElement
import pl.onewebpro.hocones.md.document.md._
import pl.onewebpro.hocones.md.document.model.MergedValuesDocument

class MergedValuesDocumentMarkdown
    extends DocumentToMdGenerator[MergedValuesDocument]
    with HoconMergedValuesOps
    with HoconConcatenationOps
    with HoconEnvironmentValueOps
    with HoconReferenceOps
    with HoconResolvedReferenceOps
    with HoconResultTypeOps
    with HoconValueOps {

  override def toMd(document: MergedValuesDocument): MarkdownElement =
    heading(document)
      .typeOfDocument(document)
      .description(document)
      .metaInformation(document.metaInformation)
      .defaultValue(document.value.defaultValue)
      .replacedValue(document.value.replacedValue)
      .toMarkdownElement
}

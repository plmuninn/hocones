package pl.onewebpro.hocones.md.document.md.documents

import net.steppschuh.markdowngenerator.MarkdownElement
import pl.onewebpro.hocones.md.document.md.{DocumentToMdGenerator, HoconValueOps}
import pl.onewebpro.hocones.md.document.model.ValueDocument

class ValueDocumentMarkdown extends DocumentToMdGenerator[ValueDocument] with HoconValueOps {

  override def toMd(document: ValueDocument): MarkdownElement =
    heading(document)
      .typeOfDocument(document)
      .typeOfValue(document.value)
      .valueOfDocument(document.value)
      .description(document)
      .metaInformation(document.metaInformation)
      .from(document.value)
      .toMarkdownElement

}

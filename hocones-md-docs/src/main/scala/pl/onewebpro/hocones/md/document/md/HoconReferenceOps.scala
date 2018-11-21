package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.text.TextBuilder
import pl.onewebpro.hocones.parser.entity.HoconReferenceValue

trait HoconReferenceOps {
  self: DocumentToMdGenerator[_] =>

  implicit class ReferenceBuilderOps(builder: TextBuilder) {
    def referenceTo(reference: HoconReferenceValue): TextBuilder =
      builder.label("Reference to:").text(reference.result.name).newParagraph()
  }
}

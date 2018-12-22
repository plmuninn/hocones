package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.text.TextBuilder
import pl.onewebpro.hocones.parser.entity.HoconReferenceValue
import pl.onewebpro.hocones.parser.entity.simple.NotResolvedRef

trait HoconReferenceOps {
  self: DocumentToMdGenerator[_] =>

  implicit class ReferenceBuilderOps(builder: TextBuilder) {

    def referenceTo(result: NotResolvedRef): TextBuilder =
      builder.label("Reference to:").text(result.name).newParagraph()

    def referenceTo(reference: HoconReferenceValue): TextBuilder =
      referenceTo(reference.result)
  }

}

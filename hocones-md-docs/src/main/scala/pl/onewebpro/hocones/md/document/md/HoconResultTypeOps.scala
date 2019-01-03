package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.text.TextBuilder
import pl.onewebpro.hocones.parser.entity.HoconResultType
import pl.onewebpro.hocones.parser.entity.simple.{EnvironmentValue, NotResolvedRef, ResolvedRef}

trait HoconResultTypeOps {
  self: DocumentToMdGenerator[_] =>

  import pl.onewebpro.hocones.parser.ops.HoconOps._

  implicit class ResultTypeBuilderOps(builder: TextBuilder) {

    def size(result: HoconResultType): TextBuilder =
      builder.label("Size:").text(result.values.size).newParagraph()

    def environments(result: HoconResultType): TextBuilder = {
      val environments = result.values.extract[EnvironmentValue]

      if (environments.isEmpty) builder
      else {
        builder
          .label("Environments:")
          .newLine()
          .unorderedList(environments.map(_.name).distinct: _*)
          .newParagraph()
      }
    }

    def references(result: HoconResultType): TextBuilder = {
      val references = result.values.extract[ResolvedRef]

      if (references.isEmpty) builder
      else {
        builder
          .label("References:")
          .newLine()
          .unorderedList(references.map(_.reference.name).distinct: _*)
          .newParagraph()
      }
    }

    def unresolvedReferences(result: HoconResultType): TextBuilder = {
      val references = result.values.extract[NotResolvedRef]

      if (references.isEmpty) builder
      else {
        builder
          .label("Unresolved references:")
          .newLine()
          .unorderedList(references.map(_.name).distinct: _*)
          .newParagraph()
      }
    }
  }

}

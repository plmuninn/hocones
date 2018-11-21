package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.text.TextBuilder
import pl.onewebpro.hocones.parser.entity.HoconConcatenation
import pl.onewebpro.hocones.parser.entity.simple.{EnvironmentValue, NotResolvedRef, ResolvedRef, SimpleHoconValue}

trait HoconConcatenationOps {
  self: DocumentToMdGenerator[_] =>

  implicit class ConcatenationBuilderOps(builder: TextBuilder) {

    private def filterAndMapSimpleValues[T <: SimpleHoconValue](values: Seq[SimpleHoconValue])
                                                               (isValue: SimpleHoconValue => Boolean): Seq[T] =
      values.filter(isValue).map(_.asInstanceOf[T])

    def pattern(reference: HoconConcatenation): TextBuilder =
      builder.label("Concatenation pattern:").text(reference.value.pattern).newParagraph()

    def environments(result: HoconConcatenation): TextBuilder = {
      val environments: Seq[EnvironmentValue] = filterAndMapSimpleValues(result.value.values) {
        case _: EnvironmentValue => true
        case _ => false
      }

      if (environments.isEmpty) builder else {
        builder
          .label("Environments:")
          .newLine()
          .unorderedList(environments.map(_.name).distinct: _*)
          .newParagraph()
      }
    }

    def references(result: HoconConcatenation): TextBuilder = {
      val references: Seq[ResolvedRef] = filterAndMapSimpleValues(result.value.values) {
        case _: ResolvedRef => true
        case _ => false
      }

      if (references.isEmpty) builder else {
        builder
          .label("References:")
          .newLine()
          .unorderedList(references.map(_.reference.name).distinct: _*)
          .newParagraph()
      }
    }

    def unresolvedReferences(result: HoconConcatenation): TextBuilder = {
      val references: Seq[NotResolvedRef] = filterAndMapSimpleValues(result.value.values) {
        case _: NotResolvedRef => true
        case _ => false
      }

      if (references.isEmpty) builder else {
        builder
          .label("Unresolved references:")
          .newLine()
          .unorderedList(references.map(_.name).distinct: _*)
          .newParagraph()
      }
    }
  }

}

package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.text.TextBuilder
import pl.onewebpro.hocones.parser.entity.HoconEnvironmentValue
import pl.onewebpro.hocones.parser.entity.simple.EnvironmentValue

trait HoconEnvironmentValueOps {
  self: DocumentToMdGenerator[_] =>

  implicit class HoconEnvironmentBuilderOps(builder: TextBuilder) {

    def isOptional(environmentValue: EnvironmentValue): TextBuilder =
      builder
        .label("Environment name:").
        text(environmentValue.name)
        .newParagraph()

    def name(environmentValue: EnvironmentValue): TextBuilder =
      builder
        .label("Is optional:")
        .text(if (environmentValue.isOptional) "True" else "False")
        .newParagraph()

    def isOptional(environment: HoconEnvironmentValue): TextBuilder = isOptional(environment.value)

    def name(environment: HoconEnvironmentValue): TextBuilder = name(environment.value)
  }

}

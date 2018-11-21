package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.text.TextBuilder
import pl.onewebpro.hocones.parser.entity.HoconEnvironmentValue

trait HoconEnvironmentValueOps {
  self: DocumentToMdGenerator[_] =>

  implicit class HoconEnvironmentBuilderOps(builder: TextBuilder) {
    def isOptional(environment: HoconEnvironmentValue): TextBuilder =
      builder
        .label("Environment name:").
        text(environment.value.name)
        .newParagraph()

    def name(environment: HoconEnvironmentValue): TextBuilder =
      builder
        .label("Is optional:")
        .text(if (environment.value.isOptional) "True" else "False")
        .newParagraph()
  }

}

package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.text.TextBuilder
import pl.onewebpro.hocones.parser.`type`.SimpleValueType
import pl.onewebpro.hocones.parser.entity.HoconValue
import pl.onewebpro.hocones.parser.entity.simple.SimpleValue

trait HoconValueOps {
  self: DocumentToMdGenerator[_] =>

  implicit class ValueTextBuilderOps(builder: TextBuilder) {
    def typeOfValue(value: HoconValue): TextBuilder = {
      val bb = builder.label("Value Type:")
      (value.valueType match {
        case SimpleValueType.UNQUOTED_STRING => bb.text("text")
        case SimpleValueType.QUOTED_STRING => bb.text("text")
        case SimpleValueType.BOOLEAN => bb.text("boolean")
        case SimpleValueType.DOUBLE => bb.text("double")
        case SimpleValueType.INT => bb.text("integer")
        case SimpleValueType.LONG => bb.text("long")
        case SimpleValueType.NULL => bb.text("null")
      }).newParagraph()
    }

    def valueOfDocument(value: SimpleValue): TextBuilder =
      builder.label("Value:").text(value.value).newParagraph()

    def valueOfDocument(value: HoconValue): TextBuilder = valueOfDocument(value.value)
  }

}

package pl.onewebpro.hocones.parser.entity.simple

import pl.onewebpro.hocones.parser.entity.simple.SimpleValue.Value
import shapeless.tag
import shapeless.tag.@@

case class SimpleValue(value: Value, wasQuoted: Boolean) extends SimpleHoconValue

object SimpleValue {

  private[SimpleValue] object SimpleValueInternal {

    trait ValueTag

  }

  import SimpleValueInternal._

  type Value = String @@ ValueTag

  //TODO test me
  private[simple] def isQuotedValue(value: String): Boolean =
    value.head.toString == "\"" && value.last.toString == "\""

  //TODO test me
  private[simple] def extractIfQuotedValue(value: String): String =
    if (isQuotedValue(value)) value.drop(1).dropRight(1) else value

  private def tagValue(value: String): Value =
    tag[ValueTag][String](extractIfQuotedValue(value))

  //TODO test me
  def apply(value: String): SimpleValue =
    new SimpleValue(tagValue(value), isQuotedValue(value))
}

package pl.onewebpro.hocones.env.model.comment

import cats.implicits._
import cats.Show
import pl.onewebpro.hocones.env.model.show.showBoolean

object MetaInformationComments {

  sealed trait MetaComment extends Comment

  object ValueTypeEnum extends Enumeration {
    type ValueType = Value
    val Object, List, Number, String = Value
  }

  import ValueTypeEnum._

  case class Type(value: ValueType) extends MetaComment
  case class Description(value: String) extends MetaComment
  case class ElementType(value: String) extends MetaComment
  case class CanBeEmpty(value: Boolean) extends MetaComment
  case class Pattern(value: String) extends MetaComment
  case class MinValue(value: Int) extends MetaComment
  case class MaxValue(value: Int) extends MetaComment
  case class MinLength(value: Int) extends MetaComment
  case class MaxLength(value: Int) extends MetaComment

  implicit val showType: Show[Type] = Show(typeV => s"Type: ${typeV.value}")

  implicit val showDescription: Show[Description] = Show(description => s"Description: ${description.value}")

  implicit val showElementType: Show[ElementType] = Show(elementType => s"Element type: ${elementType.value}")

  implicit val showCanBeEmpty: Show[CanBeEmpty] = Show(
    canBeEmpty => s"Can be empty: ${showBoolean.show(canBeEmpty.value)}"
  )

  implicit val showPattern: Show[Pattern] = Show(pattern => s"Pattern: ${pattern.value}")

  implicit val showMinValue: Show[MinValue] = Show(minValue => s"Minimum value: ${minValue.value}")

  implicit val showMaxValue: Show[MaxValue] = Show(maxValue => s"Maximum value: ${maxValue.value}")

  implicit val showMinLength: Show[MinLength] = Show(minLength => s"Minimum length: ${minLength.value}")

  implicit val showMaxLength: Show[MaxLength] = Show(maxLength => s"Maximum length: ${maxLength.value}")

  implicit val showMetaComment: Show[MetaComment] = Show {
    case value: Type        => value.show
    case value: Description => value.show
    case value: ElementType => value.show
    case value: CanBeEmpty  => value.show
    case value: Pattern     => value.show
    case value: MinValue    => value.show
    case value: MaxValue    => value.show
    case value: MinLength   => value.show
    case value: MaxLength   => value.show
  }
}

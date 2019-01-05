package pl.onewebpro.hocones.env.model
import pl.onewebpro.hocones.parser.entity._
import pl.onewebpro.hocones.parser.entity.simple.{ResolvedRef, SimpleValue}

object DefaultValue {

  private[model] def extractDefaultValue: Result => Option[String] = {
    case ResolvedRef(value: SimpleValue, _) => extractDefaultValue(value)
    case SimpleValue(value, _)              => Some(value)
    case HoconValue(_, _, _, value)         => extractDefaultValue(value)
    case HoconResolvedReference(value: HoconValue, _) =>
      extractDefaultValue(value)
    case HoconResolvedReference(value: SimpleValue, _) =>
      extractDefaultValue(value)
    case _ => None
  }

  def createDefaultValue: HoconResultValue => Option[DefaultValue] = {
    case merged: HoconMergedValues => extractDefaultValue(merged.defaultValue).map(tagDefaultValue)
    case _                         => None
  }
}

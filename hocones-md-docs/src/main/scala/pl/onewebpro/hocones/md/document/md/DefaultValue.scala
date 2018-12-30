package pl.onewebpro.hocones.md.document.md

import pl.onewebpro.hocones.parser.entity.{
  HoconResolvedReference,
  HoconValue,
  Result
}
import pl.onewebpro.hocones.parser.entity.simple.{ResolvedRef, SimpleValue}

trait DefaultValue {
  // TODO copied from env - maybe we can unify it
  def extractDefaultValue: Result => Option[String] = {
    case ResolvedRef(value: SimpleValue, _) => extractDefaultValue(value)
    case SimpleValue(value, _)              => Some(value)
    case HoconValue(_, _, _, value)         => extractDefaultValue(value)
    case HoconResolvedReference(value: HoconValue, _) =>
      extractDefaultValue(value)
    case HoconResolvedReference(value: SimpleValue, _) =>
      extractDefaultValue(value)
    case _ => None
  }
}

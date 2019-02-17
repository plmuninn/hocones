package pl.onewebpro.hocones.meta.document
import pl.onewebpro.hocones.common.DefaultValue.DefaultValue
import pl.onewebpro.hocones.parser.`type`.SimpleValueType
import pl.onewebpro.hocones.parser.`type`.SimpleValueType.SimpleValueType
import pl.onewebpro.hocones.parser.entity.simple.{ComposedConfigValue, EnvironmentValue, NotResolvedRef, ResolvedRef}
import pl.onewebpro.hocones.parser.entity.{HoconMergedValues, HoconResolvedReference, HoconResultType}

object HoconValuesOps {

  implicit class HoconSimpleValueTypeOps(value: SimpleValueType) {

    val mdName: String = value match {
      case SimpleValueType.UNQUOTED_STRING => "text"
      case SimpleValueType.QUOTED_STRING   => "text"
      case SimpleValueType.BOOLEAN         => "boolean"
      case SimpleValueType.DOUBLE          => "double"
      case SimpleValueType.INT             => "integer"
      case SimpleValueType.LONG            => "long"
      case SimpleValueType.NULL            => "null"
    }
  }

  implicit class HoconResultTypeOps(value: HoconResultType) {
    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val size: Int = value.values.size

    val environments: Iterable[EnvironmentValue] = value.values.extract[EnvironmentValue]

    val references: Iterable[ResolvedRef] = value.values.extract[ResolvedRef]

    val unresolvedReferences: Iterable[NotResolvedRef] = value.values.extract[NotResolvedRef]
  }

  implicit class ComposedConfigValueOps(value: ComposedConfigValue) {

    val environments: Iterable[EnvironmentValue] =
      value.values.collect {
        case value: EnvironmentValue => value
      }

    val references: Iterable[ResolvedRef] =
      value.values.collect {
        case value: ResolvedRef => value
      }

    val unresolvedReferences: Iterable[NotResolvedRef] =
      value.values.collect {
        case value: NotResolvedRef => value
      }
  }

  implicit class HoconResolvedReferenceOps(value: HoconResolvedReference) {

    val referenceValue: Option[DefaultValue] = value.value match {
      case merged: HoconMergedValues => merged.extractDefaultValue
      case _                         => None
    }
  }

  implicit class HoconMergedValuesOps(value: HoconMergedValues) {
    val defaultValue: Option[DefaultValue] = value.extractDefaultValue
  }
}

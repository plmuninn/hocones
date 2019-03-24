package pl.onewebpro.hocones.meta
import pl.onewebpro.hocones.common.implicits
import pl.onewebpro.hocones.meta.error.DecodingError
import pl.onewebpro.hocones.meta.model._
import pl.onewebpro.hocones.parser.`type`.SimpleValueType
import pl.onewebpro.hocones.parser.entity._
import pl.onewebpro.hocones.parser.entity.simple.SimpleHoconValue

package object codecs {

  import pl.onewebpro.hocones.common.codec.HoconesCodec.Codec
  import pl.onewebpro.hocones.common.implicits._

  implicit val resultToMetaValue: Codec[Result, MetaValue] = {
    case value: HoconResultValue =>
      val name: implicits.Name = value.path.name
      (name, value).encodeTo[MetaValue]
    case value => throw DecodingError(s"Error during encoding $value")
  }

  implicit val simpleValueToMetaValue: Codec[(Name, SimpleHoconValue), MetaValue] = {
    case (name, _) => MetaGenericInformation(name = name, description = None)
  }

  implicit val hoconArrayToMetaValue: Codec[(Name, HoconArray), MetaValue] = {
    case (name, _) => MetaList(name = name, description = None, `can-be-empty` = None, `element-type` = None)
  }

  implicit val hoconConcatenationToMetaValue: Codec[(Name, HoconConcatenation), MetaValue] = {
    case (name, _) => MetaGenericInformation(name = name, description = None)
  }

  implicit val hoconReferenceToMetaValue: Codec[(Name, HoconReferenceValue), MetaValue] = {
    case (name, _) => MetaGenericInformation(name = name, description = None)
  }

  implicit val hoconEnvironmentToMetaValue: Codec[(Name, HoconEnvironmentValue), MetaValue] = {
    case (name, _) => MetaGenericInformation(name = name, description = None)
  }

  implicit val hoconMergedValuesToMetaValue: Codec[(Name, HoconMergedValues), MetaValue] = {
    case (name, value) =>
      value.defaultValue match {
        case simpleValue: SimpleHoconValue => (name, simpleValue).encodeTo[MetaValue]
        case result                        => result.encodeTo[MetaValue]
      }
  }

  implicit val hoconResolvedReferenceToMetaValue: Codec[(Name, HoconResolvedReference), MetaValue] = {
    case (name, value) =>
      value.value match {
        case simpleValue: SimpleHoconValue => (name, simpleValue).encodeTo[MetaValue]
        case result                        => result.encodeTo[MetaValue]
      }
  }

  implicit val hoconValueToMetaValue: Codec[(Name, HoconValue), MetaValue] = {
    case (name, value) =>
      value.valueType match {
        case SimpleValueType.UNQUOTED_STRING | SimpleValueType.QUOTED_STRING | SimpleValueType.STRING =>
          MetaString(name = name, description = None, pattern = None, `min-length` = None, `max-length` = None)
        case SimpleValueType.INT | SimpleValueType.DOUBLE | SimpleValueType.LONG =>
          MetaNumber(name = name, description = None, `max-value` = None, `min-value` = None)
        case SimpleValueType.BOOLEAN | SimpleValueType.BOOLEAN =>
          MetaGenericInformation(name = name, description = None)
      }
  }

  implicit def hoconResultToMetaValue[T <: HoconResultValue]: Codec[(Name, T), MetaValue] = {
    case (name, value: SimpleHoconValue)       => (name, value).encodeTo[MetaValue]
    case (name, value: HoconArray)             => (name, value).encodeTo[MetaValue]
    case (name, value: HoconConcatenation)     => (name, value).encodeTo[MetaValue]
    case (name, value: HoconReferenceValue)    => (name, value).encodeTo[MetaValue]
    case (name, value: HoconEnvironmentValue)  => (name, value).encodeTo[MetaValue]
    case (name, value: HoconMergedValues)      => (name, value).encodeTo[MetaValue]
    case (name, value: HoconResolvedReference) => (name, value).encodeTo[MetaValue]
    case (name, value: HoconValue)             => (name, value).encodeTo[MetaValue]
    case (name, value)                         => throw DecodingError(s"Error during encoding $name: $value")
  }
}

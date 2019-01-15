package pl.onewebpro.hocones.parser.ops

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocones.common.implicits._
import pl.onewebpro.hocones.parser.HoconParser.{tagCanonicalName, CanonicalClassName}
import pl.onewebpro.hocones.parser.entity._
import pl.onewebpro.hocones.parser.entity.simple.SimpleHoconValue

import scala.reflect.ClassTag

object HoconOps {

  implicit class ConfigValueImplicits(value: ConfigValue) {

    val canonicalName: CanonicalClassName = tagCanonicalName(
      value.getClass.getCanonicalName
    )
  }

  type FlatResultList = Map[Path, HoconResultValue]

  implicit class ResultValuesOps(values: Seq[HoconResultValue]) {

    def asMap: Map[Path, HoconResultValue] =
      values.map(value => value.path -> value).toMap

    def flattenResultValues(withContainers: Boolean): FlatResultList =
      values.flatMap {
        case resultType: HoconResultType =>
          val flatten = resultType.values.flattenResultValues(withContainers)

          if (withContainers) Map(resultType.path -> resultType) ++ flatten
          else flatten
        case value => Map(value.path -> value)
      }.toMap
  }

  implicit class ExtractHoconValueOps(values: Seq[HoconResultValue]) {

    def extractWithPath[T <: SimpleHoconValue](implicit tag: ClassTag[T]): Map[Path, ExtractedValue[T]] =
      values
        .flattenResultValues(withContainers = false)
        .mapValues(ExtractHoconValue.toExtractedValues[T])
        .collect {
          case (key, Some(value)) => key -> value
        }

    def extract[T <: SimpleHoconValue](implicit tag: ClassTag[T]): Seq[T] =
      extractWithPath[T].values.flatMap(_.values).toSeq.distinct
  }

}

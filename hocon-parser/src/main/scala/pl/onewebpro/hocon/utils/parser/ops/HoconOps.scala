package pl.onewebpro.hocon.utils.parser.ops

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocon.utils.parser.HoconParser.{CanonicalClassName, Path, tagCanonicalName, tagPath}
import pl.onewebpro.hocon.utils.parser.entity._
import pl.onewebpro.hocon.utils.parser.entity.simple.{EnvironmentValue, NotResolvedRef, ResolvedRef, SimpleHoconValue}

import scala.language.implicitConversions

object HoconOps {

  implicit class ConviValueImplicits(value: ConfigValue) {
    val canonicalName: CanonicalClassName = tagCanonicalName(value.getClass.getCanonicalName)
  }

  implicit def stringToPath(value: String): Path = tagPath(value)

  type FlatResultList = Seq[(Path, HoconResultValue)]

  implicit class ResultValuesOps(values: Seq[HoconResultValue]) {
    def flattenResultValues(withContainers: Boolean): FlatResultList =
      values.flatMap {
        case resultType: HoconResultType =>
          val flatten = new ResultValuesOps(resultType.values).flattenResultValues(withContainers)

          if (withContainers) Seq(resultType.path -> resultType) ++ flatten else flatten
        case value => Iterable(value.path -> value)
      }
  }

  implicit class FlatResultListOps(map: FlatResultList) {
    def findByPath(path: Path): Option[HoconResultValue] =
      map.find {
        case (p, _) => path == p
      }.map {
        case (_, value) => value
      }
  }

  private[ops] implicit class ExtractHoconValueOps(values: Seq[HoconResultValue]) {

    private[ops] def containsValues[T <: SimpleHoconValue](implicit ex: ExtractHoconValue[T]): FlatResultList =
      values.flattenResultValues(withContainers = false)
        .filter {
          case (_, value) => ex.resultContainsValue(value)
        }

    private[ops] def extractedValues[T <: SimpleHoconValue](implicit ex: ExtractHoconValue[T]): Seq[T] =
      containsValues[T].map {
        case (_, value) => value
      }.flatMap(ex.extractValues).distinct
  }

  implicit class ExtractEnvironmentValuesOps(values: Seq[HoconResultValue]) {
    lazy val containsEnvironmentValues: FlatResultList = values.containsValues[EnvironmentValue]
    lazy val environmentValues: Seq[EnvironmentValue] = values.extractedValues[EnvironmentValue]
  }

  implicit class ExtractNotResolvedRefOps(values: Seq[HoconResultValue]) {
    lazy val containsNotResolvedValues: FlatResultList = values.containsValues[NotResolvedRef]
    lazy val notResolvedValues: Seq[NotResolvedRef] = values.extractedValues[NotResolvedRef]
  }

  implicit class ExtractResolvedRefOps(values: Seq[HoconResultValue]) {
    lazy val containsResolvedRefValues: FlatResultList = values.containsValues[ResolvedRef]
    lazy val resolvedRefValues: Seq[ResolvedRef] = values.extractedValues[ResolvedRef]
  }

}

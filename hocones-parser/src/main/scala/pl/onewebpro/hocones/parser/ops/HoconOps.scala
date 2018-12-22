package pl.onewebpro.hocones.parser.ops

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocones.common.implicits._
import pl.onewebpro.hocones.parser.HoconParser.{
  CanonicalClassName,
  tagCanonicalName
}
import pl.onewebpro.hocones.parser.entity._
import pl.onewebpro.hocones.parser.entity.simple.{
  EnvironmentValue,
  NotResolvedRef,
  ResolvedRef,
  SimpleHoconValue
}

object HoconOps {

  implicit class ConfigValueImplicits(value: ConfigValue) {
    val canonicalName: CanonicalClassName = tagCanonicalName(
      value.getClass.getCanonicalName)
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

  private[ops] implicit class ExtractHoconValueOps(
      values: Seq[HoconResultValue]) {

    private[ops] def containsValues[T <: SimpleHoconValue](
        implicit ex: ExtractHoconValue[T]): Map[Path, ExtractedValue[T]] =
      values
        .flattenResultValues(withContainers = false)
        .mapValues(ex.toExtractedValues)
        .collect {
          case (key, Some(value)) => key -> value
        }

    private[ops] def extractedValues[T <: SimpleHoconValue](
        implicit ex: ExtractHoconValue[T]): Seq[T] =
      containsValues[T].values.flatMap(_.values).toSeq.distinct
  }

  implicit class ExtractEnvironmentValuesOps(values: Seq[HoconResultValue]) {
    lazy val containsEnvironmentValues
      : Map[Path, ExtractedValue[EnvironmentValue]] =
      values.containsValues[EnvironmentValue]
    lazy val environmentValues: Seq[EnvironmentValue] =
      values.extractedValues[EnvironmentValue]
  }

  implicit class ExtractNotResolvedRefOps(values: Seq[HoconResultValue]) {
    lazy val containsNotResolvedValues
      : Map[Path, ExtractedValue[NotResolvedRef]] =
      values.containsValues[NotResolvedRef]
    lazy val notResolvedValues: Seq[NotResolvedRef] =
      values.extractedValues[NotResolvedRef]
  }

  implicit class ExtractResolvedRefOps(values: Seq[HoconResultValue]) {
    lazy val containsResolvedRefValues: Map[Path, ExtractedValue[ResolvedRef]] =
      values.containsValues[ResolvedRef]
    lazy val resolvedRefValues: Seq[ResolvedRef] =
      values.extractedValues[ResolvedRef]
  }

}

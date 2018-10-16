package pl.onewebpro.hocon.utils.parser

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocon.utils.parser.HoconParser.{CanonicalClassName, Path, tagCanonicalName}
import pl.onewebpro.hocon.utils.parser.entity._
import pl.onewebpro.hocon.utils.parser.entity.simple.{ComposedConfigValue, EnvironmentValue}

object HoconOps {

  implicit class ConviValueImplicits(value: ConfigValue) {
    val canonicalName: CanonicalClassName = tagCanonicalName(value.getClass.getCanonicalName)
  }

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

  implicit class EnvironmentValuesOps(values: Seq[HoconResultValue]) {

    private def simpleIsEnvValue: Result => Boolean = {
      case ComposedConfigValue(_, composedValues) => composedValues.exists(simpleIsEnvValue)
      case _: EnvironmentValue => true
      case _ => false
    }

    private def resultContainsEnvValue: Result => Boolean = {
      case HoconConcatenation(_, _, concatenatedValues) => concatenatedValues.values.exists(simpleIsEnvValue)
      case HoconMergedValues(_, _, default, replace) => simpleIsEnvValue(default) || simpleIsEnvValue(replace)
      case HoconArray(_, _, arrayValues) => arrayValues.exists(resultContainsEnvValue)
      case HoconObject(_, _, objectValues) => objectValues.exists(resultContainsEnvValue)
      case _: HoconEnvironmentValue => true
      case _ => false
    }

    lazy val containsEnvironmentValues: FlatResultList =
      values.flattenResultValues(withContainers = false)
        .filter {
          case (_, value) => resultContainsEnvValue(value)
        }

    private def extractEnvironmentValues: HoconResultValue => Iterable[EnvironmentValue] = {
      case HoconConcatenation(_, _, concatenatedValues) =>
        concatenatedValues.values.flatMap {
          case value: EnvironmentValue => Iterable(value)
          case _ => Nil
        }
      case HoconMergedValues(_, _, default, replace) =>
        Iterable(default, replace).flatMap {
          case value: EnvironmentValue => Iterable(value)
          case _ => Nil
        }
      case HoconArray(_, _, arrayValues) => arrayValues.flatMap(extractEnvironmentValues)
      case HoconObject(_, _, objectValues) => objectValues.flatMap(extractEnvironmentValues)
      case HoconEnvironmentValue(_, _, value) => Iterable(value)
    }

    lazy val environmentValues: Seq[EnvironmentValue] =
      containsEnvironmentValues.map {
        case (_, value) => value
      }.flatMap(extractEnvironmentValues).distinct
  }

}

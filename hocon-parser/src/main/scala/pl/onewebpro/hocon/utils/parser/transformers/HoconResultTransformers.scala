package pl.onewebpro.hocon.utils.parser.transformers

//import pl.onewebpro.hocon.utils.parser.HoconParser.Path
//import pl.onewebpro.hocon.utils.parser.entity.simple.{ComposedConfigValue, EnvironmentValue, SimpleHoconValue}
//import pl.onewebpro.hocon.utils.parser.entity._
//import pl.onewebpro.hocon.utils.parser.result.HoconResult

object HoconResultTransformers {

//  implicit class HoconEnvironmentResultsTransofrmer(results: HoconResult) {
//    private def simpleIsEnvValue: SimpleHoconValue => Boolean = {
//      case ComposedConfigValue(_, values) => values.exists(simpleIsEnvValue)
//      case _: EnvironmentValue => true
//      case _ => false
//    }
//
//    private def resultContainsEnvValue: HoconResultValue => Boolean = {
//      case HoconConcatenation(_, _, values) => values.values.exists(simpleIsEnvValue)
//      case HoconMergedValues(_, _, default, replace) => simpleIsEnvValue(default) || simpleIsEnvValue(replace)
//      case HoconArray(_, _, values) => values.exists(resultContainsEnvValue)
//      case HoconObject(_, _, values) => values.exists(resultContainsEnvValue)
//      case _: HoconEnvironmentValue => true
//      case _ => false
//    }
//
//    private def extractEnvironmentValues: HoconResultValue => Iterable[EnvironmentValue] = {
//      case HoconConcatenation(_, _, values) =>
//        values.values.flatMap {
//          case value: EnvironmentValue => Iterable(value)
//          case _ => Nil
//        }
//      case HoconMergedValues(_, _, default, replace) =>
//        Iterable(default, replace).flatMap {
//          case value: EnvironmentValue => Iterable(value)
//          case _ => Nil
//        }
//      case HoconArray(_, _, values) => values.flatMap(extractEnvironmentValues)
//      case HoconObject(_, _, values) => values.flatMap(extractEnvironmentValues)
//      case HoconEnvironmentValue(_, _, value) => Iterable(value)
//    }
//
//    def onlyWithEnvironments: Stream[(Path, HoconResultValue)] =
//      results.flatten(false).filter {
//        case (_, value) => resultContainsEnvValue(value)
//      }
//
//    def onlyEnvironments: Stream[EnvironmentValue] =
//      onlyWithEnvironments.map {
//        case (_, value) => value
//      }.flatMap(extractEnvironmentValues).distinct
//  }

}

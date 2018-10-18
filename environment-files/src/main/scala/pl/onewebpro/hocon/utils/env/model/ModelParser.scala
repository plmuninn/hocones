package pl.onewebpro.hocon.utils.env.model

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocon.utils.env.config.Configuration.EnvironmentConfiguration
import pl.onewebpro.hocon.utils.parser.HoconParser.Path
import pl.onewebpro.hocon.utils.parser.HoconResult
import pl.onewebpro.hocon.utils.parser.entity._
import pl.onewebpro.hocon.utils.parser.entity.simple.{ComposedConfigValue, ResolvedRef, SimpleValue, EnvironmentValue => SimpleEnvironmentValue}

object ModelParser {

  import pl.onewebpro.hocon.utils.parser.ops.HoconOps._

  private[model] def createComments(path: Path, cfg: ConfigValue, value: SimpleEnvironmentValue): Iterable[String] =
    Iterable(
      Some(s"Path : $path"),
      Option(cfg.origin().filename()).map(fileName => s"From file: $fileName"),
      Some(s"Optional: ${value.isOptional}"),
    ).flatten.map("#" + _)

  private[model] def createEnvironmentValue(path: Path,
                                            cfg: ConfigValue,
                                            value: SimpleEnvironmentValue,
                                            defaultValue: Option[String])
                                           (implicit config: EnvironmentConfiguration): EnvironmentValue = {

    val default = if (config.withDefaults) defaultValue else None
    val comments = if (config.withComments) createComments(path, cfg, value) else Nil

    EnvironmentValue(name = value.name, defaultValue = default, comments = comments)
  }

  private[model] def createDefaultValue: Result => Option[String] = {
    case ResolvedRef(value: SimpleValue, _) => Some(value.value)
    case SimpleValue(value, _) => Some(value)
    case HoconValue(_, _, _, value) => Some(value.value)
    case HoconResolvedReference(value: HoconValue, _) => Some(value.value.value)
    case HoconResolvedReference(value: SimpleValue, _) => Some(value.value)
    case _ => None
  }

  private[model] def asLocalModel(acc: Iterable[EnvironmentValue], value: HoconResultValue)
                                 (implicit config: EnvironmentConfiguration): Iterable[EnvironmentValue] =
    value match {
      case array: HoconArray => acc ++ array.values.flatMap(asLocalModel(acc, _))
      case concatenation: HoconConcatenation => acc ++
        concatenation.value.values.flatMap {
          case env: SimpleEnvironmentValue =>
            Iterable(createEnvironmentValue(concatenation.path, concatenation.cfg, env, None))
          case _ => Nil
        }
      case environment: HoconEnvironmentValue =>
        acc ++ Iterable(createEnvironmentValue(environment.path, environment.cfg, environment.value, None))
      case merged: HoconMergedValues =>
        val defaultValue = if (config.withDefaults) createDefaultValue(merged.defaultValue) else None
        acc ++ Iterable(merged.replacedValue, merged.defaultValue).flatMap {
          case value: HoconResultValue =>
            asLocalModel(acc, value).map(model => model.copy(defaultValue = model.defaultValue.orElse(defaultValue)))
          case value: SimpleEnvironmentValue => Iterable(createEnvironmentValue(merged.path, merged.cfg, value, defaultValue))
          case value: ComposedConfigValue =>
            value.values.filter {
              case _: SimpleEnvironmentValue => true
              case _ => false
            }.map {
              case value: SimpleEnvironmentValue => createEnvironmentValue(merged.path, merged.cfg, value, defaultValue)
            }
          case _ => Nil
        }
      case `object`: HoconObject => acc ++ `object`.values.flatMap(asLocalModel(acc, _))
      case _ => acc
    }

  def apply(config: EnvironmentConfiguration, result: HoconResult): Iterable[EnvironmentValue] = {
    implicit val cfg: EnvironmentConfiguration = config

    result.results.containsEnvironmentValues.map {
      case (_, value) => value
    }.foldLeft(Iterable.empty[EnvironmentValue])(asLocalModel)
      .groupBy(_.name).map(_._2.head)
  }
}

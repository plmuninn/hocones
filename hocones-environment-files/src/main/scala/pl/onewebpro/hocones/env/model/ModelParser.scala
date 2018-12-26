package pl.onewebpro.hocones.env.model

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocones.env.config.Configuration.EnvironmentConfiguration
import pl.onewebpro.hocones.common.implicits.Path
import pl.onewebpro.hocones.parser.HoconResult
import pl.onewebpro.hocones.parser.entity._
import pl.onewebpro.hocones.parser.entity.simple.{ResolvedRef, SimpleValue, EnvironmentValue => SimpleEnvironmentValue}
import pl.onewebpro.hocones.parser.ops.ExtractedValue

object ModelParser {

  import pl.onewebpro.hocones.parser.ops.HoconOps._

  private[model] def createComments(path: Path, cfg: ConfigValue, value: SimpleEnvironmentValue): Iterable[String] =
    Iterable(
      Some(s"Path : $path"),
      Option(cfg.origin().filename()).map(fileName => s"From file: $fileName"),
      Some(s"Optional: ${value.isOptional}"),
    ).flatten.map("#" + _)

  private[model] def createEnvironmentValue(
      path: Path,
      cfg: ConfigValue,
      value: SimpleEnvironmentValue,
      defaultValue: Option[String])(implicit config: EnvironmentConfiguration): EnvironmentValue = {

    val default = if (config.withDefaults) defaultValue else None
    val comments =
      if (config.withComments) createComments(path, cfg, value) else Nil

    EnvironmentValue(name = value.name, defaultValue = default, comments = comments)
  }

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

  private[model] def createDefaultValue: HoconResultValue => Option[String] = {
    case merged: HoconMergedValues => extractDefaultValue(merged.defaultValue)
    case _                         => None
  }

  private[model] def asLocalModel
    : (Path, ExtractedValue[SimpleEnvironmentValue]) => EnvironmentConfiguration => Iterable[EnvironmentValue] = {
    case (path, ExtractedValue(cfg, parent, values)) =>
      implicit config: EnvironmentConfiguration =>
        val defaultValue = createDefaultValue(parent)
        values.map(value => createEnvironmentValue(path, cfg, value, defaultValue))
  }

  def removeDuplicates(values: Iterable[EnvironmentValue]): Iterable[EnvironmentValue] =
    values.foldLeft(Vector.empty[EnvironmentValue]) {
      case (acc, value) =>
        def compareName: EnvironmentValue => Boolean = _.name == value.name

        acc.find(compareName) match {
          case Some(foundValue) =>
            if (foundValue.defaultValue.isEmpty && value.defaultValue.nonEmpty)
              acc.updated(acc.indexWhere(compareName), value)
            else
              acc
          case None => acc :+ value
        }

    }

  def parse(config: EnvironmentConfiguration, result: HoconResult): Iterable[EnvironmentValue] = {
    implicit val cfg: EnvironmentConfiguration = config

    val values =
      result.results.containsEnvironmentValues
        .map {
          case (path, value) => path -> asLocalModel(path, value)(cfg)
        }
        .values
        .flatten

    if (cfg.removeDuplicates) removeDuplicates(values) else values
  }
}

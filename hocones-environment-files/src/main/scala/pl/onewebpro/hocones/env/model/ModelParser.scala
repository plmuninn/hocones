package pl.onewebpro.hocones.env.model

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocones.env.config.Configuration.EnvironmentConfiguration
import pl.onewebpro.hocones.common.implicits.Path
import pl.onewebpro.hocones.meta.model._
import pl.onewebpro.hocones.parser.HoconResult
import pl.onewebpro.hocones.parser.entity._
import pl.onewebpro.hocones.parser.entity.simple.{ResolvedRef, SimpleValue, EnvironmentValue => SimpleEnvironmentValue}
import pl.onewebpro.hocones.parser.ops.ExtractedValue

object ModelParser {

  import pl.onewebpro.hocones.parser.ops.HoconOps._

  private[model] def createMetaFields: Option[MetaValue] => Iterable[Option[String]] = {
    case Some(value: MetaObject) =>
      Iterable(
        Some("Type: Object"),
        value.description.map(description => s"Description: $description"),
        value.`element-type`.map(elementType => s"Element type: $elementType")
      )
    case Some(value: MetaList) =>
      Iterable(
        Some("Type: List"),
        value.description.map(description => s"Description: $description"),
        value.`can-be-empty`.map(canBeEmpty => s"Can be empty: $canBeEmpty"),
        value.`element-type`.map(elementType => s"Element type: $elementType")
      )
    case Some(value: MetaNumber) =>
      Iterable(
        Some("Type: Number"),
        value.description.map(description => s"Description: $description"),
        value.`min-value`.map(minValue => s"Minimum value: $minValue"),
        value.`max-value`.map(maxValue => s"Maximum value: $maxValue")
      )
    case Some(value: MetaString) =>
      Iterable(
        Some("Type: String"),
        value.description.map(description => s"Description: $description"),
        value.pattern.map(pattern => s"Pattern: $pattern"),
        value.`min-length`.map(minLength => s"Minimum length: $minLength"),
        value.`max-length`.map(maxLength => s"Maximum length: $maxLength")
      )
    case Some(value: MetaGenericInformation) =>
      Iterable(value.description.map(description => s"Description: $description"))
    case Some(value: MetaValue) =>
      Iterable(value.description.map(description => s"Description: $description"))
    case _ => Iterable()
  }

  private[model] def createComments(path: Path,
                                    cfg: ConfigValue,
                                    value: SimpleEnvironmentValue,
                                    metaValue: Option[MetaValue]): Iterable[String] =
    (Iterable(
      Some(s"Path : $path"),
      Option(cfg.origin().filename()).map(fileName => s"From file: $fileName"),
      Some(s"Optional: ${value.isOptional}"),
    ) ++ createMetaFields(metaValue)).flatten.map("# " + _)

  private[model] def createEnvironmentValue(
      path: Path,
      cfg: ConfigValue,
      value: SimpleEnvironmentValue,
      defaultValue: Option[String],
      metaValue: Option[MetaValue])(implicit config: EnvironmentConfiguration): EnvironmentValue = {

    val default = if (config.withDefaults) defaultValue else None
    val comments =
      if (config.withComments) createComments(path, cfg, value, metaValue) else Nil

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

  private[model] def asLocalModel: (Path,
                                    ExtractedValue[SimpleEnvironmentValue],
                                    MetaInformation) => EnvironmentConfiguration => Iterable[EnvironmentValue] = {
    case (path, ExtractedValue(cfg, parent, values), metaInformation) =>
      implicit config: EnvironmentConfiguration =>
        val defaultValue = createDefaultValue(parent)
        values.map(
          value =>
            createEnvironmentValue(path,
                                   cfg,
                                   value,
                                   defaultValue,
                                   if (config.displayMeta) metaInformation.findByPathAndName(path) else None))
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

  def parse(config: EnvironmentConfiguration,
            result: HoconResult,
            meta: MetaInformation): Iterable[EnvironmentValue] = {
    implicit val cfg: EnvironmentConfiguration = config

    val values =
      result.results.containsEnvironmentValues
        .map {
          case (path, value) => path -> asLocalModel(path, value, meta)(cfg)
        }
        .values
        .flatten

    if (cfg.removeDuplicates) removeDuplicates(values) else values
  }
}

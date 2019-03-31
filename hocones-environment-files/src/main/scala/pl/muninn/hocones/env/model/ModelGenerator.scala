package pl.muninn.hocones.env.model

import pl.muninn.hocones.common.DefaultValue.DefaultValue
import pl.muninn.hocones.common.implicits.{Path => HPath}
import pl.muninn.hocones.env.config.Configuration.EnvironmentConfiguration
import pl.muninn.hocones.env.model.comment.CommentsGenerator
import pl.muninn.hocones.meta.model.{MetaInformation, MetaValue}
import pl.muninn.hocones.parser.HoconResult
import pl.muninn.hocones.parser.entity.simple.{EnvironmentValue => HoconEnvironmentValue}
import pl.muninn.hocones.parser.ops.{DefaultValue, ExtractedValue}

object ModelGenerator {

  import pl.muninn.hocones.parser.ops.HoconOps._

  private[model] def createEnvironmentValues
    : (HPath, ExtractedValue[HoconEnvironmentValue], MetaInformation) => EnvironmentConfiguration => Iterable[
      EnvironmentValue
    ] = {
    case (path, ExtractedValue(cfg, parent, values), metaInformation) =>
      implicit config: EnvironmentConfiguration =>
        val defaultValue: Option[DefaultValue] =
          if (config.withDefaults) DefaultValue.createDefaultValue(parent) else None

        values.map { value =>
          val metaValue: Option[MetaValue] = metaInformation.findByPathAndName(path)
          val comments: Iterable[comment.Comment] =
            if (config.withComments) CommentsGenerator.createComments(path, cfg, value, metaValue) else Nil

          EnvironmentValue(name = tagName(value.name), defaultValue = defaultValue, comments = comments)
        }
  }

  private[model] def removeDuplicates(values: Iterable[EnvironmentValue]): Iterable[EnvironmentValue] =
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

  private[model] def orderValues: Iterable[EnvironmentValue] => Iterable[EnvironmentValue] =
    _.toList.sortBy(_.name.toString)

  def generate(
    config: EnvironmentConfiguration,
    result: HoconResult,
    meta: MetaInformation
  ): Iterable[EnvironmentValue] = {
    val values: Iterable[EnvironmentValue] =
      result.results
        .extractWithPath[HoconEnvironmentValue]
        .flatMap {
          case (path, value) => createEnvironmentValues(path, value, meta)(config)
        }

    val transformResults: Iterable[EnvironmentValue] => Iterable[EnvironmentValue] =
      if (config.removeDuplicates) (removeDuplicates _).compose(orderValues) else orderValues

    transformResults(values)
  }
}

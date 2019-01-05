package pl.onewebpro.hocones.env.model

import pl.onewebpro.hocones.common.implicits.{Path => HPath}
import pl.onewebpro.hocones.env.config.Configuration.EnvironmentConfiguration
import pl.onewebpro.hocones.env.model.comment.CommentsGenerator
import pl.onewebpro.hocones.meta.model._
import pl.onewebpro.hocones.parser.HoconResult
import pl.onewebpro.hocones.parser.entity.simple.{EnvironmentValue => SimpleEnvironmentValue}
import pl.onewebpro.hocones.parser.ops.ExtractedValue

object ModelParser {

  import pl.onewebpro.hocones.parser.ops.HoconOps._

  private[model] def createEnvironmentValues
    : (HPath, ExtractedValue[SimpleEnvironmentValue], MetaInformation) => EnvironmentConfiguration => Iterable[
      EnvironmentValue
    ] = {
    case (path, ExtractedValue(cfg, parent, values), metaInformation) =>
      implicit config: EnvironmentConfiguration =>
        val defaultValue: Option[DefaultValue] = DefaultValue.createDefaultValue(parent)

        values.map { value =>
          val metaValue: Option[MetaValue] = if (config.displayMeta) metaInformation.findByPathAndName(path) else None
          val comments: Iterable[comment.Comment] =
            if (config.withComments) CommentsGenerator.createComments(path, cfg, value, metaValue) else Nil

          EnvironmentValue(name = tagName(value.name), defaultValue = defaultValue, comments = comments)
        }
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

  def parse(
    config: EnvironmentConfiguration,
    result: HoconResult,
    meta: MetaInformation
  ): Iterable[EnvironmentValue] = {
    val values: Iterable[EnvironmentValue] =
      result.results
        .extractWithPath[SimpleEnvironmentValue]
        .flatMap {
          case (path, value) => createEnvironmentValues(path, value, meta)(config)
        }

    if (config.removeDuplicates) removeDuplicates(values) else values
  }
}

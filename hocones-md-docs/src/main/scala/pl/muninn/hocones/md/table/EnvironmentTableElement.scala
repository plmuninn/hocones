package pl.muninn.hocones.md.table

import cats.effect.SyncIO
import pl.muninn.hocones.common.DefaultValue.DefaultValue
import pl.muninn.hocones.common.implicits.Path
import pl.muninn.hocones.meta.document.model.Documentation
import pl.muninn.hocones.meta.model.{MetaInformation, MetaValue}
import pl.muninn.hocones.parser.HoconResult
import pl.muninn.hocones.parser.entity.HoconResultValue
import pl.muninn.hocones.parser.entity.simple.EnvironmentValue
import pl.muninn.hocones.parser.entity.simple.EnvironmentValue.EnvName
import pl.muninn.hocones.parser.ops.DefaultValue

case class EnvironmentTableElement(
  environmentVariable: String,
  description: Option[String],
  defaultValue: Option[String],
  details: String,
  isOptional: Boolean,
  path: Path
)

object EnvironmentTableElement {

  import pl.muninn.hocones.parser.ops.HoconOps._

  def generate(
    result: HoconResult,
    meta: MetaInformation,
    documentation: Documentation
  ): SyncIO[Seq[EnvironmentTableElement]] =
    SyncIO(result.results.extractWithPath[EnvironmentValue])
      .map { values =>
        values.flatMap {
          case (path, model) =>
            model.values.map(
              environment => generateTableElement(meta, documentation, path, model.parent, environment)
            )
        }.toList
      }
      .map(removeDuplicates)
      .map(orderElements)

  def generateTableElement(
    meta: MetaInformation,
    documentation: Documentation,
    path: Path,
    parent: HoconResultValue,
    environment: EnvironmentValue
  ): EnvironmentTableElement = {
    val name: EnvName = environment.name
    val metaValue: Option[MetaValue] = meta.findByPathAndName(pathWithName = path)
    val description: Option[String] = metaValue.flatMap(_.description)
    val defaultValue: Option[DefaultValue] = DefaultValue.createDefaultValue(parent)
    val details: String =
      metaValue
        .flatMap(documentation.findByMetaValue)
        .map(_.details)
        .getOrElse(Map.empty)
        .map {
          case (key, value) => s"$key:$value"
        }
        .mkString("; ")

    EnvironmentTableElement(
      environmentVariable = name,
      description = description,
      defaultValue = defaultValue,
      details = details,
      isOptional = environment.isOptional,
      path = path
    )
  }

  def orderElements(elements: Seq[EnvironmentTableElement]): Seq[EnvironmentTableElement] =
    elements.sortBy(_.environmentVariable)

  def removeDuplicates(values: Seq[EnvironmentTableElement]): Seq[EnvironmentTableElement] =
    values.foldLeft(Vector.empty[EnvironmentTableElement]) {
      case (acc, element) =>
        acc
          .find(value => value.path == element.path && value.environmentVariable == value.environmentVariable) match {
          case Some(_) =>
            acc // TODO make some validation and value higher elements with description etc
          case None => acc :+ element
        }
    }
}

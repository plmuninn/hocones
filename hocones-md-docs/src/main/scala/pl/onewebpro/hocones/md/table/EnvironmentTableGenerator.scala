package pl.onewebpro.hocones.md.table

import cats.effect.SyncIO
import pl.onewebpro.hocones.common.DefaultValue.DefaultValue
import pl.onewebpro.hocones.common.implicits.Path
import pl.onewebpro.hocones.meta.document.model.Documentation
import pl.onewebpro.hocones.meta.model._
import pl.onewebpro.hocones.parser.HoconResult
import pl.onewebpro.hocones.parser.entity._
import pl.onewebpro.hocones.parser.entity.simple.EnvironmentValue
import pl.onewebpro.hocones.parser.entity.simple.EnvironmentValue.EnvName
import pl.onewebpro.hocones.parser.ops.DefaultValue

object EnvironmentTableGenerator {

  import pl.onewebpro.hocones.parser.ops.HoconOps._

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

  def generateTable(values: Seq[EnvironmentTableElement]): String = {
    import pl.muninn.scalamdtag._

    markdown(
      h1("Configuration environments"),
      table(
        ("Environment", "Description", "Default", "Details", "Is optional", "Path"),
        values.map(EnvironmentTableElement.unapply).collect {
          case Some(tupled) => tupled
        }
      )
    ).md
  }

  def generate(
    result: HoconResult,
    meta: MetaInformation,
    documentation: Documentation
  ): SyncIO[String] =
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
      .map(generateTable)

}

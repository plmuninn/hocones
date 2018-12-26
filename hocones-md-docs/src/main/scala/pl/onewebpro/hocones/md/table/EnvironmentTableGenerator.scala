package pl.onewebpro.hocones.md.table

import cats.effect.SyncIO
import cats.implicits._
import pl.onewebpro.hocones.common.implicits.Path
import pl.onewebpro.hocones.md.document.md.{DefaultValue, MetaValueDocumentation}
import pl.onewebpro.hocones.md.table.model.EnvironmentTableElement
import pl.onewebpro.hocones.meta.model._
import pl.onewebpro.hocones.parser.HoconResult
import pl.onewebpro.hocones.parser.entity._
import pl.onewebpro.hocones.parser.entity.simple.EnvironmentValue

object EnvironmentTableGenerator extends MetaValueDocumentation with DefaultValue {

  import pl.onewebpro.hocones.parser.ops.HoconOps._

  def getDefaultValue: HoconResultValue => Option[String] = {
    case HoconMergedValues(_, _, defaultValue, _) =>
      extractDefaultValue(defaultValue)
    case _ => None
  }

  def generateTableElement(meta: MetaInformation,
                           path: Path,
                           parent: HoconResultValue,
                           value: EnvironmentValue): SyncIO[EnvironmentTableElement] =
    for {
      name <- SyncIO.pure(value.name)
      meta <- SyncIO(meta.findByPathAndName(pathWithName = path))
      description <- SyncIO(meta.flatMap(_.description))
      defaultValue <- SyncIO(getDefaultValue(parent))
      details <- SyncIO(getDetails(meta))
    } yield
      EnvironmentTableElement(
        environmentVariable = name,
        description = description,
        defaultValue = defaultValue,
        details = details,
        isOptional = value.isOptional,
        path = path
      )

  def removeDuplicates(values: Seq[EnvironmentTableElement]): SyncIO[Seq[EnvironmentTableElement]] =
    SyncIO.pure(values).map { values =>
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

  def apply(result: HoconResult, meta: MetaInformation): SyncIO[Seq[EnvironmentTableElement]] =
    SyncIO(result.results.containsEnvironmentValues)
      .flatMap { values =>
        values
          .flatMap {
            case (path, model) =>
              model.values.map(environment => generateTableElement(meta, path, model.parent, environment))
          }
          .toList
          .sequence
      }
      .flatMap(removeDuplicates)

}

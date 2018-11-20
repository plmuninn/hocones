package pl.onewebpro.hocones.md.table

import cats.effect.SyncIO
import cats.implicits._
import pl.onewebpro.hocones.md.table.model.EnvironmentTableElement
import pl.onewebpro.hocones.meta.model._
import pl.onewebpro.hocones.parser.HoconParser.Path
import pl.onewebpro.hocones.parser.HoconResult
import pl.onewebpro.hocones.parser.entity._
import pl.onewebpro.hocones.parser.entity.simple.{EnvironmentValue, ResolvedRef, SimpleValue}

import scala.language.implicitConversions

object EnvironmentTableGenerator {

  import pl.onewebpro.hocones.parser.ops.HoconOps._

  // TODO copied from env - maybe we can unify it
  private def extractDefaultValue: Result => Option[String] = {
    case ResolvedRef(value: SimpleValue, _) => extractDefaultValue(value)
    case SimpleValue(value, _) => Some(value)
    case HoconValue(_, _, _, value) => extractDefaultValue(value)
    case HoconResolvedReference(value: HoconValue, _) => extractDefaultValue(value)
    case HoconResolvedReference(value: SimpleValue, _) => extractDefaultValue(value)
    case _ => None
  }

  def getDefaultValue: HoconResultValue => Option[String] = {
    case HoconMergedValues(_, _, defaultValue, _) => extractDefaultValue(defaultValue)
    case _ => None
  }

  // Flatten for Options
  private implicit def flat[K, V](kv: (K, Option[V])) = kv._2.map(kv._1 -> _).toList

  def getDetails: Option[MetaValue] => Map[String, String] = {
    case Some(model: MetaString) => Map("pattern" -> model.pattern, "min-length" -> model.`min-length`.map(_.toString), "max-length" -> model.`max-length`.map(_.toString)).flatten.toMap
    case Some(model: MetaNumber) => Map("max-value" -> model.`max-value`.map(_.toString), "min-value" -> model.`min-value`.map(_.toString)).flatten.toMap
    case Some(model: MetaList) => Map("can-be-empty" -> model.`can-be-empty`.map(_.toString), "element-type" -> model.`element-type`).flatten.toMap
    case Some(model: MetaObject) => Map("element-type" -> model.`element-type`).flatten.toMap
    case Some(_) => Map.empty
    case None => Map.empty
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
    } yield EnvironmentTableElement(
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
          acc.find(value => value.path == element.path && value.environmentVariable == value.environmentVariable) match {
            case Some(_) => acc // TODO make some validation and value higher elements with description etc
            case None => acc :+ element
          }
      }
    }

  def apply(result: HoconResult, meta: MetaInformation): SyncIO[Seq[EnvironmentTableElement]] =
    SyncIO(result.results.containsEnvironmentValues).flatMap { values =>
      values.flatMap {
        case (path, model) => model.values.map(environment => generateTableElement(meta, path, model.parent, environment))
      }.toList.sequence
    }.flatMap(removeDuplicates)

}

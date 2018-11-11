package pl.onewebpro.hocones.meta

import cats.effect.SyncIO
import cats.implicits._
import pl.onewebpro.hocones.meta.BuildInfo.version
import pl.onewebpro.hocones.meta.model._
import pl.onewebpro.hocones.parser.HoconParser.Path
import pl.onewebpro.hocones.parser.HoconResult
import pl.onewebpro.hocones.parser.`type`.SimpleValueType
import pl.onewebpro.hocones.parser.entity._

object MetaParser {

  private[MetaParser] object InternalMetaParser {

    implicit class PathFunctions(path: Path) {
      lazy val splitPath: Array[String] = path.split("\\.")

      lazy val name: String = splitPath.last

      lazy val packageName: String = splitPath.dropRight(1).mkString(".")

      lazy val isOrphan: Boolean = splitPath.length == 1
    }

  }

  import InternalMetaParser._

  private[meta] def mapResultValue: Result => SyncIO[MetaValue] = {
    case result: HoconResultValue =>
      val name = result.path.name
      result match {
        case _: HoconArray => SyncIO.pure(MetaList(name = name, description = None, `can-be-empty` = None, `element-type` = None))
        case _: HoconConcatenation => SyncIO.pure(MetaConcatenation(name = name, description = None))
        case _: HoconEnvironmentValue => SyncIO.pure(MetaEnvironment(name = name, description = None))
        case _: HoconObject => SyncIO.pure(MetaObject(name = name, description = None, `element-type` = None))
        case _: HoconReferenceValue => SyncIO.pure(MetaUntypeInformation(name = name, description = None))

        case value: HoconMergedValues => mapResultValue(value.defaultValue)
        case value: HoconResolvedReference => mapResultValue(value.value)

        case value: HoconValue => value.valueType match {
          case SimpleValueType.UNQUOTED_STRING => SyncIO.pure(MetaString(name = name, description = None, pattern = None, `min-length` = None, `max-length` = None))
          case SimpleValueType.QUOTED_STRING => SyncIO.pure(MetaString(name = name, description = None, pattern = None, `min-length` = None, `max-length` = None))
          case SimpleValueType.BOOLEAN => SyncIO.pure(MetaBoolean(name = name, description = None))
          case SimpleValueType.DOUBLE => SyncIO.pure(MetaNumber(name = name, description = None, `max-value` = None, `min-value` = None))
          case SimpleValueType.INT => SyncIO.pure(MetaNumber(name = name, description = None, `max-value` = None, `min-value` = None))
          case SimpleValueType.LONG => SyncIO.pure(MetaNumber(name = name, description = None, `max-value` = None, `min-value` = None))
          case SimpleValueType.NULL => SyncIO.pure(MetaUntypeInformation(name = name, description = None))
        }
      }
    case _ => SyncIO.raiseError(MetaParsingError("Wrong type of value"))
  }

  private[meta] def orphans(hocones: HoconResult): SyncIO[Seq[MetaValue]] =
    for {
      results <- SyncIO.pure(hocones.results)
      orphans <- results.filter(_.path.isOrphan).map(mapResultValue).toList.sequence
    } yield orphans

  private[meta] def roots(hocones: HoconResult): SyncIO[Map[String, Map[String, Seq[MetaValue]]]] =
    SyncIO.pure(Map.empty) // TODO

  def generate(hocones: HoconResult): SyncIO[MetaInformation] =
    for {
      rootsResult <- roots(hocones)
      orphansResult <- orphans(hocones)
    } yield MetaInformation(version, rootsResult, orphansResult)
}

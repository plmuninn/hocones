package pl.onewebpro.hocones.meta

import cats.effect.SyncIO
import cats.implicits._
import pl.onewebpro.hocones.meta.BuildInfo.version
import pl.onewebpro.hocones.meta.model._
import pl.onewebpro.hocones.parser.HoconParser.Path
import pl.onewebpro.hocones.parser.HoconResult
import pl.onewebpro.hocones.parser.`type`.SimpleValueType
import pl.onewebpro.hocones.parser.entity._
import pl.onewebpro.hocones.parser.entity.simple.SimpleHoconValue
import pl.onewebpro.hocones.parser.ops.HoconOps._

object MetaParser {

  private[MetaParser] object InternalMetaParser {

    implicit class PathFunctions(path: Path) {
      lazy val splitPath: Array[String] = path.split("\\.")

      lazy val name: String = splitPath.last

      lazy val packageName: Path = dropRight(1)

      lazy val isOrphan: Boolean = splitPath.length == 1

      def dropRight(elements: Int): Path = splitPath.dropRight(elements).mkString(".")
    }

    implicit class PathResultFunctions(pair: (Path, HoconResultValue)) {
      lazy val (path, value) = pair

      def isOrphan: Boolean = path.isOrphan
    }

  }

  import InternalMetaParser._

  private[meta] def mapSimpleHoconValue(name: String, value: SimpleHoconValue): SyncIO[MetaValue] =
    SyncIO.pure(MetaUntypeInformation(name = name, description = None))

  private[meta] def mapResultValue: Result => SyncIO[MetaValue] = {
    case result: HoconResultValue =>
      val name = result.path.name
      result match {
        case _: HoconArray => SyncIO.pure(MetaList(name = name, description = None, `can-be-empty` = None, `element-type` = None))
        case _: HoconConcatenation => SyncIO.pure(MetaConcatenation(name = name, description = None))
        case _: HoconEnvironmentValue => SyncIO.pure(MetaEnvironment(name = name, description = None))
        case _: HoconObject => SyncIO.pure(MetaObject(name = name, description = None, `element-type` = None))
        case _: HoconReferenceValue => SyncIO.pure(MetaUntypeInformation(name = name, description = None))

        case value: HoconMergedValues => value.defaultValue match {
          case defaultValue: SimpleHoconValue => mapSimpleHoconValue(name, defaultValue)
          case defaultValue => mapResultValue(defaultValue)
        }
        case value: HoconResolvedReference => value.value match {
          case referencedValue: SimpleHoconValue => mapSimpleHoconValue(name, referencedValue)
          case referencedValue => mapResultValue(referencedValue)
        }

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

  // TODO make it more IO
  private[meta] def generateRoots(hocones: HoconResult): SyncIO[Seq[String]] = {
    val resultKeys = hocones.results.asMap.filterNot(_.isOrphan).keys

    if (resultKeys.isEmpty) SyncIO.pure(Nil) else SyncIO {
      resultKeys.foldLeft(Seq.empty[String]) {
        case (acc, path) =>
          val packageName = path.packageName.dropRight(1)

          acc.zipWithIndex.find { case (rootPath, _) => packageName.contains(rootPath) } match {
            case Some((rootPath, index)) =>
              val root: Path = rootPath
              val fromPath = path.splitPath
              val result: String = root.splitPath.filter(part => fromPath.contains(part)).mkString(".")
              acc.updated(index, result)

            case _ => acc :+ packageName
          }
      }
    }
  }

  //TODO make it more IO
  private[meta] def generateMetaValues(roots: Seq[String],
                                       hocones: HoconResult): SyncIO[Map[String, Map[String, Seq[MetaValue]]]] = SyncIO {
    val result = roots.map(path => path -> Map.empty[String, Seq[MetaValue]]).toMap

    hocones.results.asMap.filterNot(_.isOrphan).foldLeft(result) {
      case (acc, (path, value)) =>
        acc.find {
          case (key, _) => path.contains(key)
        } match {
          case Some((key, cont)) =>
            val res: Map[String, Seq[MetaValue]] = cont.find {
              case (subKey, _) =>
                val actualPath = key + "." + subKey
                val cleared: Path = path.replace(s"$actualPath.", "")
                cleared.splitPath.length == 1
            } match {
              case Some((subPath, valueAcc)) => cont + (subPath -> (valueAcc :+ mapResultValue(value).unsafeRunSync()))
              case _ =>
                val packageName = path.packageName.replace(s"$key.", "")
                cont + (packageName -> Seq(mapResultValue(value).unsafeRunSync()))
            }

            acc + (key -> res)
          case _ => throw new Exception("Something went wrong, not root found")
        }
    }

  }

  private[meta] def roots(hocones: HoconResult): SyncIO[Map[String, Map[String, Seq[MetaValue]]]] =
    for {
      rootsKeys <- generateRoots(hocones)
      generatedMetaValues <- generateMetaValues(rootsKeys, hocones)
    } yield generatedMetaValues

  def generate(hocones: HoconResult): SyncIO[MetaInformation] =
    for {
      rootsResult <- roots(hocones)
      orphansResult <- orphans(hocones)
    } yield MetaInformation(version, rootsResult, orphansResult)
}

package pl.onewebpro.hocon.utils.parser

import java.util.Map.Entry

import cats.effect.IO
import cats.implicits._
import com.typesafe.config._
import com.typesafe.scalalogging.LazyLogging
import pl.onewebpro.hocon.utils.parser.entity.`type`.{ResultType, SimpleValueType, ValueType}
import pl.onewebpro.hocon.utils.parser.entity.`type`.ResultType.ResultType
import pl.onewebpro.hocon.utils.parser.entity.`type`.SimpleValueType.SimpleValueType
import pl.onewebpro.hocon.utils.parser.entity.`type`.ValueType.ValueType
import pl.onewebpro.hocon.utils.parser.entity.simple._
import pl.onewebpro.hocon.utils.parser.entity._
import pl.onewebpro.hocon.utils.parser.result.HoconResult
import shapeless.tag
import shapeless.tag.@@

import scala.collection.JavaConverters._

object HoconParser extends LazyLogging {

  private[HoconParser] object InternalHoconParser {

    trait PathTag

    trait CanonicalClassNameTag

    trait RenderedValueTag

  }

  import InternalHoconParser._

  type Path = String @@ PathTag

  def tagPath(path: String): Path = tag[PathTag][String](path)

  type CanonicalClassName = String @@ CanonicalClassNameTag

  private[parser] def tagCanonicalName(name: String): CanonicalClassName = tag[CanonicalClassNameTag][String](name)

  type RenderedValue = String @@ RenderedValueTag

  private[parser] def tagRenderedValue(value: String): RenderedValue = tag[RenderedValueTag][String](value)

  private val renderOptions = ConfigRenderOptions.concise().setFormatted(true)

  private val simpleValueTypes = SimpleValueType.values.map(_.toString)
  private val valueTypes = ValueType.values.map(_.toString)
  private val resultType = ResultType.values.map(_.toString)

  //TODO test me
  private[parser] def divideRenderedValue(renderedValue: String): List[String] =
    renderedValue.lines.toList.map(_.replace("\",", "\""))

  //TODO test me
  private[parser] def extractClassName(value: ConfigValue): CanonicalClassName =
    tagCanonicalName(value.getClass.getCanonicalName)

  //TODO test me
  private[parser] def mapEntryToTuple(entry: Entry[String, ConfigValue]): (Path, ConfigValue, CanonicalClassName) = {
    val path = tagPath(entry.getKey)
    val value = entry.getValue
    val className = extractClassName(value)

    (path, value, className)
  }

  //TODO test me
  private[parser] def render(configValue: ConfigValue): RenderedValue = tagRenderedValue(configValue.render(renderOptions))

  //TODO test me
  private[parser] def parseSimpleValue(renderedValue: RenderedValue, path: Path, configValue: ConfigValue, valueType: SimpleValueType): IO[HoconResultValue] =
    for {
      value <- IO(SimpleValue(renderedValue))
    } yield HoconValue(path, configValue, valueType, value)

  private[parser] def divideToLeftAndRight(values: List[String]): IO[(String, String)] = {
    def createListString: List[String] => String = "[" + _.drop(1).dropRight(1).mkString(",") + "]"

    def getSingleValue(v: List[String]): IO[(String, List[String])] = for {
      startIndex <- IO(v.indexOf("["))
      result <-
        if (startIndex == -1) IO((v.mkString(""), Nil))
        else if (startIndex == 0) {
          val endIndex: Int = {
            val withSemicolon = v.indexOf("],")
            if (withSemicolon == -1) v.indexOf("]") else withSemicolon
          }
          val (left, right) = v.splitAt(endIndex + 1)
          IO((createListString(left), right))
        }
        else {
          val (left, right) = v.splitAt(startIndex)
          IO(left.mkString(""), right)
        }
    } yield result

    for {
      leftResultAndRest <- getSingleValue(values)
      (leftResult, rest) = leftResultAndRest
      rightResultAndRest <- getSingleValue(rest)
      (rightResult, _) = rightResultAndRest
    } yield (leftResult, rightResult)
  }


  private[parser] def parseAsValue(path: Path, value: String): IO[ConfigValue] = for {
    parsed <- IO {
      ConfigFactory.parseString(
        s"""
           | $path : $value
       """.stripMargin)
    }
  } yield parsed.entrySet().asScala.find(_.getKey == path).map(_.getValue).get // TODO WTF is that

  private[parser] def parseMergeableArrays(path: Path, value: ValueType, renderedValue: RenderedValue, configValue: ConfigValue)
                                          (implicit cfg: Config): IO[HoconResultValue] = for {
    asList <- IO(divideRenderedValue(renderedValue))
    leftAndRight <- divideToLeftAndRight(asList)
    (leftSide, rightSide) = leftAndRight
    leftValue <- parseAsValue(path, leftSide)
    rigthValue <- parseAsValue(path, rightSide)
    leftValue <- parseValue(path, leftValue, extractClassName(leftValue))
    rigthValue <- parseValue(path, rigthValue, extractClassName(rigthValue))
  } yield HoconMergedValues(path, configValue, leftValue, rigthValue)

  //TODO test me
  private[parser] def parseValueType(path: Path, value: ValueType, renderedValue: RenderedValue, configValue: ConfigValue)
                                    (implicit cfg: Config): IO[HoconResultValue] =
    value match {
      case ValueType.CONCATENATION => for {
        composedValue <- ComposedConfigValue(renderedValue)
      } yield HoconConcatenation(path, configValue, composedValue)
      case ValueType.REFERENCE =>
        SimpleHoconValue(renderedValue).flatMap {
          case result: EnvironmentValue => IO.pure(HoconEnvironmentValue(path, configValue, result))
          case result: NotResolvedRef => IO.pure(HoconReferenceValue(path, configValue, result))
          case result => IO.raiseError(ParsingError(s"Wrong value of ${result.getClass.getCanonicalName}"))
        }
      case ValueType.MERGE =>
        renderedValue match {
          case rendered if rendered.contains("[") && rendered.contains("]") =>
            parseMergeableArrays(path, value, renderedValue, configValue)
          case _ =>
            divideRenderedValue(renderedValue) match {
              case left :: right :: Nil =>
                for {
                  leftHoconValue <- SimpleHoconValue(left)
                  rightHoconValue <- SimpleHoconValue(right)
                } yield HoconMergedValues(path, configValue, leftHoconValue, rightHoconValue)
              case _ => IO.raiseError(ParsingError(s"Value $renderedValue is not handled merge value"))
            }
        }
    }

  //TODO test me
  private[parser] def parseResultType(path: Path, value: ResultType, configValue: ConfigValue)
                                     (implicit cfg: Config): IO[HoconResultValue] = value match {
    case ResultType.LIST => configValue match {
      case list: ConfigList => for {
        values <- list.asScala.toList.zipWithIndex.map {
          case (listConfigValue, index) =>
            val (tag, canonicalName) = (tagPath(path + s".$index"), extractClassName(listConfigValue))
            parseValue(tag, listConfigValue, canonicalName)
        }.sequence
      } yield HoconArray(path, configValue, values)
      case _ => IO.raiseError(ParsingError(s"Something is wrong $path is not ConfigList"))
    }
    case ResultType.OBJECT => configValue match {
      case hoconObject: ConfigObject => for {
        entrySet <- IO(hoconObject.entrySet().asScala)
        tupled <- IO(entrySet.map(mapEntryToTuple).toSet)
        withExtraPath <- IO(tupled.map { case (objectPath, objectValue, name) => (tagPath(path + "." + objectPath), objectValue, name) })
        results <- parseEntrySet(withExtraPath)
      } yield HoconObject(path, configValue, results)
      case _ => IO.raiseError(ParsingError(s"Something is wrong $path is not ConfigList"))
    }
  }

  //TODO test me
  private[parser] def parseValue(value: (Path, ConfigValue, CanonicalClassName))(implicit cfg: Config): IO[HoconResultValue] = {
    val (path, configValue, className) = value
    lazy val renderedValue = render(configValue)

    logger.info(s"Parsing path $path")
    logger.debug(value.toString())

    if (simpleValueTypes.contains(className)) parseSimpleValue(renderedValue, path, configValue, SimpleValueType.withName(className))
    else if (valueTypes.contains(className)) parseValueType(path, ValueType.withName(className), renderedValue, configValue)
    else if (resultType.contains(className)) parseResultType(path, ResultType.withName(className), configValue)
    else IO.raiseError(ParsingError(s"Unhandled type $className on path $path"))
  }

  //TODO test me
  private[parser] def parseEntrySet(values: Set[(Path, ConfigValue, CanonicalClassName)])
                                   (implicit cfg: Config): IO[List[HoconResultValue]] =
    values.toList.map(parseValue).sequence

  def apply(config: Config): IO[HoconResult] = {
    implicit val cfg: Config = config

    for {
      entrySet <- IO(config.entrySet().asScala)
      tupled <- IO(entrySet.map(mapEntryToTuple))
      results <- parseEntrySet(tupled.toSet)
      resolved <- HoconReferenceResolver(results)
    } yield HoconResult(config, resolved)
  }
}

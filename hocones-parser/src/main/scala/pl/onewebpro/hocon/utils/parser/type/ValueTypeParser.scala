package pl.onewebpro.hocon.utils.parser.`type`

import cats.data.OptionT
import cats.effect.IO
import com.typesafe.config.{Config, ConfigFactory, ConfigValue}
import pl.onewebpro.hocon.utils.parser.HoconParser.{Path, RenderedValue}
import pl.onewebpro.hocon.utils.parser.{HoconParser, ParsingError}
import pl.onewebpro.hocon.utils.parser.`type`.ValueType.ValueType
import pl.onewebpro.hocon.utils.parser.entity._
import pl.onewebpro.hocon.utils.parser.entity.simple.{ComposedConfigValue, EnvironmentValue, NotResolvedRef, SimpleHoconValue}

import scala.collection.JavaConverters._


object ValueTypeParser {

  import pl.onewebpro.hocon.utils.parser.ops.HoconOps._

  private def divideRenderedValue(renderedValue: String): List[String] =
    renderedValue.lines.toList.map(_.replace("\",", "\""))

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

  private[parser] def parseAsValue(path: Path, value: String): IO[ConfigValue] =
    (for {
      parsed <- OptionT.liftF(IO {
        ConfigFactory.parseString(
          s"""
             | $path : $value
       """.stripMargin)
      })
      value <- OptionT(IO(parsed.entrySet().asScala.find(_.getKey == path).map(_.getValue)))
    } yield value).value.flatMap {
      case Some(result) => IO.pure(result)
      case None => IO.raiseError(ParsingError(s"There was problem with parsing array value $path:$value"))
    }

  private[parser] def parseMergeableArrays(path: Path, value: ValueType, renderedValue: RenderedValue, configValue: ConfigValue)
                                          (implicit cfg: Config): IO[HoconResultValue] = for {
    asList <- IO(divideRenderedValue(renderedValue))
    leftAndRight <- divideToLeftAndRight(asList)
    (leftSide, rightSide) = leftAndRight
    leftValue <- parseAsValue(path, leftSide)
    rigthValue <- parseAsValue(path, rightSide)
    leftValue <- HoconParser.parseValue(path, leftValue, leftValue.canonicalName)
    rigthValue <- HoconParser.parseValue(path, rigthValue, rigthValue.canonicalName)
  } yield HoconMergedValues(path, configValue, leftValue, rigthValue)

  //TODO test me
  def parse(path: Path, value: ValueType, renderedValue: RenderedValue, configValue: ConfigValue)
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
}

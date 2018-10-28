package pl.onewebpro.hocones.parser

import java.util.Map.Entry

import cats.effect.IO
import cats.implicits._
import com.typesafe.config._
import com.typesafe.scalalogging.LazyLogging
import pl.onewebpro.hocones.parser.`type`._
import pl.onewebpro.hocones.parser.entity._
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
  import pl.onewebpro.hocones.parser.ops.HoconOps._

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
  private[parser] def mapEntryToTuple(entry: Entry[String, ConfigValue]): (Path, ConfigValue, CanonicalClassName) = {
    val path = tagPath(entry.getKey)
    val value = entry.getValue

    (path, entry.getValue, value.canonicalName)
  }

  //TODO test me
  private[parser] def render(configValue: ConfigValue): RenderedValue = tagRenderedValue(configValue.render(renderOptions))

  //TODO test me
  def parseValue(value: (Path, ConfigValue, CanonicalClassName))(implicit cfg: Config): IO[HoconResultValue] = {
    val (path, configValue, className) = value
    lazy val renderedValue = render(configValue)

    logger.info(s"Parsing path $path")
    logger.debug(value.toString())

    if (simpleValueTypes.contains(className)) SimpleValueParser.parse(renderedValue, path, configValue, SimpleValueType.withName(className))
    else if (valueTypes.contains(className)) ValueTypeParser.parse(path, ValueType.withName(className), renderedValue, configValue)
    else if (resultType.contains(className)) ResultTypeParser.parse(path, ResultType.withName(className), configValue)
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

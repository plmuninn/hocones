package pl.onewebpro.hocon.utils.parser.entity.simple

import cats.effect.IO
import pl.onewebpro.hocon.utils.parser.entity.simple.ComposedConfigValue.HoconPattern
import cats.implicits._
import pl.onewebpro.hocon.utils.parser.ParsingError
import shapeless.tag
import shapeless.tag.@@

case class ComposedConfigValue(pattern: HoconPattern, values: Seq[SimpleHoconValue]) extends SimpleHoconValue

object ComposedConfigValue {

  private[simple] object ComposedConfigValueInternal {

    trait PatternTag

  }

  import ComposedConfigValueInternal._

  type HoconPattern = String @@ PatternTag


  private def tagPattern(pattern: String): HoconPattern = tag[PatternTag][String](pattern)

  //TODO test me
  private[simple] def isComposedValue(value: String): Boolean =
    EnvironmentValue.containsEnv(value) && EnvironmentValue.envRegex.replaceAllIn(value, "").nonEmpty

  //TODO test me
  private[simple] def extractValues(value: String): IO[List[SimpleHoconValue]] = {
    type Acc = List[IO[SimpleHoconValue]]

    def split(str: String, acc: Acc): Acc = {
      EnvironmentValue.envRegex.findFirstMatchIn(str) match {
        case Some(result) => {
          val name = result.subgroups.last
          val hoconValue: IO[SimpleHoconValue] = SimpleHoconValue(name)
          val sb = new StringBuilder(str)

          // If value is ont start
          if(result.start == 0) {
            sb.delete(result.start, result.end)
            val without = sb.toString()
            // Add value on start and then split rest
            (acc :+ hoconValue) ++ split(without, acc)
          } else {
            val (beginning, end) = sb.splitAt(result.start)
            end.delete(0, beginning.size - 1)
            // If value was on end, split rest and value on end
            (acc ++ split(beginning.toString(), acc) :+ hoconValue) ++ split(end.toString(), acc)
          }
        }
        case None => if(str.isEmpty) acc else acc :+ IO.pure(SimpleValue(str))
      }
    }

    split(value, Nil).sequence
  }

  //TODO test me
  def apply(value: String): IO[ComposedConfigValue] =
    if (!isComposedValue(value)) IO.raiseError(ParsingError(s"Value $value is not composed value")) else for {
      pattern <- IO(tagPattern(value))
      values <- extractValues(value)
    } yield new ComposedConfigValue(pattern, values)

}

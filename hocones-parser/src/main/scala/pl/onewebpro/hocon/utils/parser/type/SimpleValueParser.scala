package pl.onewebpro.hocon.utils.parser.`type`

import cats.effect.IO
import com.typesafe.config.ConfigValue
import pl.onewebpro.hocon.utils.parser.HoconParser.{Path, RenderedValue}
import pl.onewebpro.hocon.utils.parser.`type`.SimpleValueType.SimpleValueType
import pl.onewebpro.hocon.utils.parser.entity.{HoconResultValue, HoconValue}
import pl.onewebpro.hocon.utils.parser.entity.simple.SimpleValue

object SimpleValueParser {

  def parse(renderedValue: RenderedValue, path: Path, configValue: ConfigValue, valueType: SimpleValueType): IO[HoconResultValue] =
    for {
      value <- IO(SimpleValue(renderedValue))
    } yield HoconValue(path, configValue, valueType, value)
  
}

package pl.muninn.hocones.parser.`type`

import cats.effect.IO
import com.typesafe.config.ConfigValue
import pl.muninn.hocones.common.implicits.Path
import pl.muninn.hocones.parser.HoconParser.RenderedValue
import pl.muninn.hocones.parser.`type`.SimpleValueType.SimpleValueType
import pl.muninn.hocones.parser.entity.{HoconResultValue, HoconValue}
import pl.muninn.hocones.parser.entity.simple.SimpleValue

object SimpleValueParser {

  def parse(
    renderedValue: RenderedValue,
    path: Path,
    configValue: ConfigValue,
    valueType: SimpleValueType
  ): IO[HoconResultValue] =
    for {
      value <- IO(SimpleValue(renderedValue))
    } yield HoconValue(path, configValue, valueType, value)

}

package pl.onewebpro.hocones.parser.`type`

import cats.effect.IO
import com.typesafe.config.ConfigValue
import pl.onewebpro.hocones.common.implicits.Path
import pl.onewebpro.hocones.parser.HoconParser.RenderedValue
import pl.onewebpro.hocones.parser.`type`.SimpleValueType.SimpleValueType
import pl.onewebpro.hocones.parser.entity.{HoconResultValue, HoconValue}
import pl.onewebpro.hocones.parser.entity.simple.SimpleValue

object SimpleValueParser {

  def parse(renderedValue: RenderedValue,
            path: Path,
            configValue: ConfigValue,
            valueType: SimpleValueType): IO[HoconResultValue] =
    for {
      value <- IO(SimpleValue(renderedValue))
    } yield HoconValue(path, configValue, valueType, value)

}

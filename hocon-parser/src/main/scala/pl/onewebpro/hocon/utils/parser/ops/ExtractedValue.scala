package pl.onewebpro.hocon.utils.parser.ops

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocon.utils.parser.entity.HoconResultValue

case class ExtractedValue[T](cfg: ConfigValue, parent: HoconResultValue, values: Iterable[T])
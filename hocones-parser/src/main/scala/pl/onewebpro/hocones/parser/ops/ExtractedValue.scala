package pl.onewebpro.hocones.parser.ops

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocones.parser.entity.HoconResultValue

case class ExtractedValue[T](cfg: ConfigValue, parent: HoconResultValue, values: Iterable[T])

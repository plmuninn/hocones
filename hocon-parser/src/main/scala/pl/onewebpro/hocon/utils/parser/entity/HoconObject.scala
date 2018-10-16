package pl.onewebpro.hocon.utils.parser.entity

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocon.utils.parser.HoconParser.Path

case class HoconObject(path: Path, cfg: ConfigValue, values: Seq[HoconResultValue]) extends HoconResultType

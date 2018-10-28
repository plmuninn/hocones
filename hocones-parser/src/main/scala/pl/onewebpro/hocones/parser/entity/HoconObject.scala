package pl.onewebpro.hocones.parser.entity

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocones.parser.HoconParser.Path

case class HoconObject(path: Path, cfg: ConfigValue, values: Seq[HoconResultValue]) extends HoconResultType

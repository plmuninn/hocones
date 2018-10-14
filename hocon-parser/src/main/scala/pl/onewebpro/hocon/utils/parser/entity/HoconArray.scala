package pl.onewebpro.hocon.utils.parser.entity

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocon.utils.parser.HoconParser.Path

case class HoconArray(path: Path, cfg: ConfigValue, values: Iterable[Result]) extends HoconResultType
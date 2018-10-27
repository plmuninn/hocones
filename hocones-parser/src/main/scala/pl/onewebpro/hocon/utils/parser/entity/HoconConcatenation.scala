package pl.onewebpro.hocon.utils.parser.entity

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocon.utils.parser.HoconParser.Path
import pl.onewebpro.hocon.utils.parser.entity.simple.ComposedConfigValue

case class HoconConcatenation(path: Path, cfg: ConfigValue, value: ComposedConfigValue) extends HoconValueType

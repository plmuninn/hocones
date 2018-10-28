package pl.onewebpro.hocones.parser.entity

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocones.parser.HoconParser.Path
import pl.onewebpro.hocones.parser.entity.simple.ComposedConfigValue

case class HoconConcatenation(path: Path, cfg: ConfigValue, value: ComposedConfigValue) extends HoconValueType

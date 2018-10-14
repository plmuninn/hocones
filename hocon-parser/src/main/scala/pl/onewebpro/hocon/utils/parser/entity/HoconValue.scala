package pl.onewebpro.hocon.utils.parser.entity

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocon.utils.parser.HoconParser.Path
import pl.onewebpro.hocon.utils.parser.entity.`type`.SimpleValueType.SimpleValueType
import pl.onewebpro.hocon.utils.parser.entity.simple.SimpleValue

case class HoconValue(path: Path, cfg: ConfigValue, valueType: SimpleValueType, value: SimpleValue) extends HoconSimpleValueType

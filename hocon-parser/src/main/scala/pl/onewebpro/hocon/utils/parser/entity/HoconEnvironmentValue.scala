package pl.onewebpro.hocon.utils.parser.entity

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocon.utils.parser.HoconParser.Path
import pl.onewebpro.hocon.utils.parser.entity.simple.EnvironmentValue

case class HoconEnvironmentValue(path:Path, cfg:ConfigValue, value:EnvironmentValue) extends HoconSimpleValueType

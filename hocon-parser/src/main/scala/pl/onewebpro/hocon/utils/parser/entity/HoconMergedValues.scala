package pl.onewebpro.hocon.utils.parser.entity

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocon.utils.parser.HoconParser.Path

case class HoconMergedValues(path: Path,
                             cfg: ConfigValue,
                             defaultValue: Result,
                             replacedValue: Result) extends HoconValueType



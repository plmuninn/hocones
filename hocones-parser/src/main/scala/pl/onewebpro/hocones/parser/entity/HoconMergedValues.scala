package pl.onewebpro.hocones.parser.entity

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocones.parser.HoconParser.Path

case class HoconMergedValues(path: Path,
                             cfg: ConfigValue,
                             defaultValue: Result,
                             replacedValue: Result) extends HoconValueType



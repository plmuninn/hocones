package pl.onewebpro.hocones.parser.entity

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocones.common.implicits.Path

case class HoconMergedValues(path: Path, cfg: ConfigValue, defaultValue: Result, replacedValue: Result)
    extends HoconValueType

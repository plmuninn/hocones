package pl.onewebpro.hocones.parser.entity

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocones.common.implicits.Path
import pl.onewebpro.hocones.parser.`type`.SimpleValueType.SimpleValueType
import pl.onewebpro.hocones.parser.entity.simple.SimpleValue

case class HoconValue(path: Path,
                      cfg: ConfigValue,
                      valueType: SimpleValueType,
                      value: SimpleValue)
    extends HoconSimpleValueType

package pl.muninn.hocones.parser.entity

import com.typesafe.config.ConfigValue
import pl.muninn.hocones.common.implicits.Path
import pl.muninn.hocones.parser.`type`.SimpleValueType.SimpleValueType
import pl.muninn.hocones.parser.entity.simple.SimpleValue

case class HoconValue(path: Path, cfg: ConfigValue, valueType: SimpleValueType, value: SimpleValue)
    extends HoconSimpleValueType

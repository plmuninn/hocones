package pl.muninn.hocones.parser.entity

import com.typesafe.config.ConfigValue
import pl.muninn.hocones.common.implicits.Path
import pl.muninn.hocones.parser.entity.simple.EnvironmentValue

case class HoconEnvironmentValue(path: Path, cfg: ConfigValue, value: EnvironmentValue) extends HoconSimpleValueType

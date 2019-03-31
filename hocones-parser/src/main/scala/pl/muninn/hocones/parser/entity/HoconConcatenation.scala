package pl.muninn.hocones.parser.entity

import com.typesafe.config.ConfigValue
import pl.muninn.hocones.common.implicits.Path
import pl.muninn.hocones.parser.entity.simple.ComposedConfigValue

case class HoconConcatenation(path: Path, cfg: ConfigValue, value: ComposedConfigValue) extends HoconValueType

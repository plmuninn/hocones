package pl.onewebpro.hocones.parser.entity

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocones.common.implicits.Path
import pl.onewebpro.hocones.parser.entity.simple.EnvironmentValue

case class HoconEnvironmentValue(path: Path,
                                 cfg: ConfigValue,
                                 value: EnvironmentValue)
    extends HoconSimpleValueType

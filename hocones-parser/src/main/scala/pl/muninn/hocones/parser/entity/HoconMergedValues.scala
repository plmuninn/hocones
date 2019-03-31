package pl.muninn.hocones.parser.entity

import com.typesafe.config.ConfigValue
import pl.muninn.hocones.common.implicits.Path
import pl.muninn.hocones.parser.ops.DefaultValue
import pl.muninn.hocones.common.DefaultValue.DefaultValue

case class HoconMergedValues(path: Path, cfg: ConfigValue, defaultValue: Result, replacedValue: Result)
    extends HoconValueType {
  def extractDefaultValue: Option[DefaultValue] = DefaultValue.createDefaultValue(defaultValue)
}

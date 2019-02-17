package pl.onewebpro.hocones.parser.entity

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocones.common.implicits.Path
import pl.onewebpro.hocones.parser.ops.DefaultValue
import pl.onewebpro.hocones.common.DefaultValue.DefaultValue

case class HoconMergedValues(path: Path, cfg: ConfigValue, defaultValue: Result, replacedValue: Result)
    extends HoconValueType {
  def extractDefaultValue: Option[DefaultValue] = DefaultValue.createDefaultValue(defaultValue)
}

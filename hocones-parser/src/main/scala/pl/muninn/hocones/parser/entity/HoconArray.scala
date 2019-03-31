package pl.muninn.hocones.parser.entity

import com.typesafe.config.ConfigValue
import pl.muninn.hocones.common.implicits.Path

case class HoconArray(path: Path, cfg: ConfigValue, values: Seq[HoconResultValue]) extends HoconResultType

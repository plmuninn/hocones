package pl.muninn.hocones.parser.entity

import com.typesafe.config.ConfigValue
import pl.muninn.hocones.common.implicits.Path
import pl.muninn.hocones.parser.entity.simple.NotResolvedRef

case class HoconReferenceValue(path: Path, cfg: ConfigValue, result: NotResolvedRef) extends HoconValueType

package pl.onewebpro.hocones.parser.entity

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocones.parser.HoconParser.Path
import pl.onewebpro.hocones.parser.entity.simple.NotResolvedRef

case class HoconReferenceValue(path: Path, cfg: ConfigValue, result: NotResolvedRef) extends HoconValueType

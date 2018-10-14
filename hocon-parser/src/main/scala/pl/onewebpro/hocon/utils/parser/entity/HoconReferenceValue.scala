package pl.onewebpro.hocon.utils.parser.entity

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocon.utils.parser.HoconParser.Path
import pl.onewebpro.hocon.utils.parser.entity.simple.NotResolvedRef

case class HoconReferenceValue(path: Path, cfg: ConfigValue, result: NotResolvedRef) extends HoconValueType

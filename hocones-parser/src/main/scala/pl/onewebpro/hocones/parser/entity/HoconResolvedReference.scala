package pl.onewebpro.hocones.parser.entity

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocones.common.implicits.Path

case class HoconResolvedReference(value: Result, reference: HoconReferenceValue) extends HoconValueType {
  override lazy val path: Path = reference.path
  lazy val cfg: ConfigValue = reference.cfg
}

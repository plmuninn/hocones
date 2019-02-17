package pl.onewebpro.hocones.md.table

import pl.onewebpro.hocones.common.implicits.Path

case class EnvironmentTableElement(
  environmentVariable: String,
  description: Option[String],
  defaultValue: Option[String],
  details: String,
  isOptional: Boolean,
  path: Path
)

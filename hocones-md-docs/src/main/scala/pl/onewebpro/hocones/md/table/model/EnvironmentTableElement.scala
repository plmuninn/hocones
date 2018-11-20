package pl.onewebpro.hocones.md.table.model

import pl.onewebpro.hocones.parser.HoconParser.Path

case class EnvironmentTableElement(environmentVariable: String,
                                   description: Option[String],
                                   defaultValue: Option[String],
                                   details: Map[String, String],
                                   isOptional: Boolean,
                                   path: Path)

package pl.muninn.hocones.common

import shapeless.tag
import shapeless.tag.@@

object DefaultValue {

  sealed trait DefaultValueTag

  type DefaultValue = String @@ DefaultValueTag

  def tagDefaultValue(defaultValue: String): DefaultValue = tag[DefaultValueTag][String](defaultValue)
}

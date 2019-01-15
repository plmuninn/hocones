package pl.onewebpro.hocones.env

import shapeless.tag
import shapeless.tag.@@

package object model {

  sealed trait NameTag

  sealed trait DefaultValueTag

  type Name = String @@ NameTag

  type DefaultValue = String @@ DefaultValueTag

  def tagName(name: String): Name = tag[NameTag][String](name)

  def tagDefaultValue(defaultValue: String): DefaultValue = tag[DefaultValueTag][String](defaultValue)
}

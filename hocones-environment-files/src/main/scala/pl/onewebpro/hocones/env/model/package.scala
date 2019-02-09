package pl.onewebpro.hocones.env

import shapeless.tag
import shapeless.tag.@@

package object model {

  sealed trait NameTag

  type Name = String @@ NameTag

  def tagName(name: String): Name = tag[NameTag][String](name)
}

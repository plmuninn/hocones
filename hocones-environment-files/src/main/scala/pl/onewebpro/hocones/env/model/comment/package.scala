package pl.onewebpro.hocones.env.model

import pl.onewebpro.hocones.common.implicits.{Path => HPath}

package object comment {
  trait Comment

  case class Path(value: HPath) extends Comment
  case class FileName(value: String) extends Comment
  case class IsOptional(value: Boolean) extends Comment
}

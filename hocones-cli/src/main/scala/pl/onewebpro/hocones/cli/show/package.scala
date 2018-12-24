package pl.onewebpro.hocones.cli
import cats.Show
import fansi.Str

package object show {
  implicit val showStr: Show[Str] = Show.show(_.toString())

}

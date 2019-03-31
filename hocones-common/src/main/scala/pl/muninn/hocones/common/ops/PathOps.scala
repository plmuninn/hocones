package pl.muninn.hocones.common.ops

import shapeless.tag
import shapeless.tag.@@

import scala.language.implicitConversions

trait PathOps {

  private[PathOps] object InternalPathOps {

    trait PathTag

    trait NameTag

  }

  import InternalPathOps._

  type Path = String @@ PathTag

  type Name = String @@ NameTag

  def tagPath(path: String): Path = tag[PathTag][String](path)

  def tagName(name: String): Name = tag[NameTag][String](name)

  implicit def stringToPath(value: String): Path = tagPath(value)

  implicit class PathFunctions(path: Path) {

    implicit private def stringToName(value: String): Name = tagName(value)

    lazy val splitPath: Array[String] = path.split("\\.")

    lazy val name: Name =
      if (splitPath.length == 2) splitPath.mkString(".") else splitPath.last

    lazy val packageName: Path = dropRight(1)

    lazy val isOrphan: Boolean = splitPath.length <= 2

    def dropRight(elements: Int): Path =
      splitPath.dropRight(elements).mkString(".")
  }

}

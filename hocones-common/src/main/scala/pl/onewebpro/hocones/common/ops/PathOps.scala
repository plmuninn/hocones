package pl.onewebpro.hocones.common.ops

import shapeless.tag
import shapeless.tag.@@

import scala.language.implicitConversions

trait PathOps {

  private[PathOps] object InternalPathOps {

    trait PathTag

  }

  import InternalPathOps._

  type Path = String @@ PathTag

  def tagPath(path: String): Path = tag[PathTag][String](path)

  implicit def stringToPath(value: String): Path = tagPath(value)

  implicit class PathFunctions(path: Path) {
    lazy val splitPath: Array[String] = path.split("\\.")

    lazy val name: String = if(splitPath.length == 2) splitPath.mkString(".") else splitPath.last

    lazy val packageName: Path = dropRight(1)

    lazy val isOrphan: Boolean = splitPath.length <= 2

    def dropRight(elements: Int): Path = splitPath.dropRight(elements).mkString(".")
  }

}
